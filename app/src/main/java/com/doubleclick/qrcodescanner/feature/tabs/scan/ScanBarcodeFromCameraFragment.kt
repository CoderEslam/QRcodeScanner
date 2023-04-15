@file:Suppress("DEPRECATION")
package com.doubleclick.qrcodescanner.feature.tabs.scan
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.doubleclick.qrcodescanner.R
import com.doubleclick.qrcodescanner.databinding.FragmentScanBarcodeFromCameraBinding
import com.doubleclick.qrcodescanner.di.barcodeParser
import com.doubleclick.qrcodescanner.di.settings
import com.doubleclick.qrcodescanner.di.scannerCameraHelper
import com.doubleclick.qrcodescanner.di.barcodeDatabase
import com.doubleclick.qrcodescanner.di.permissionsHelper
import com.doubleclick.qrcodescanner.extension.applySystemWindowInsets
import com.doubleclick.qrcodescanner.extension.showError
import com.doubleclick.qrcodescanner.extension.equalTo
import com.doubleclick.qrcodescanner.extension.vibrator
import com.doubleclick.qrcodescanner.extension.vibrateOnce
import com.doubleclick.qrcodescanner.feature.barcode.BarcodeActivity
import com.doubleclick.qrcodescanner.feature.common.dialog.ConfirmBarcodeDialogFragment
import com.doubleclick.qrcodescanner.feature.tabs.scan.file.ScanBarcodeFromFileActivity
import com.doubleclick.qrcodescanner.model.Barcode
import com.doubleclick.qrcodescanner.usecase.SupportedBarcodeFormats
import com.doubleclick.qrcodescanner.usecase.save
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
class ScanBarcodeFromCameraFragment : Fragment(), ConfirmBarcodeDialogFragment.Listener {
    private lateinit var _binding: FragmentScanBarcodeFromCameraBinding
    private val binding get() = _binding
    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_REQUEST_CODE = 101
        private const val ZXING_SCAN_INTENT_ACTION = "com.google.zxing.client.android.SCAN"
        private const val CONTINUOUS_SCANNING_PREVIEW_DELAY = 500L
    }
    private val vibrationPattern = arrayOf<Long>(0, 350).toLongArray()
    private val disposable = CompositeDisposable()
    private var maxZoom: Int = 0
    private val zoomStep = 5
    private lateinit var codeScanner: CodeScanner
    private var toast: Toast? = null
    private var lastResult: Barcode? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScanBarcodeFromCameraBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
        setDarkStatusBar()
        initScanner()
        initFlashButton()
        handleScanFromFileClicked()
        handleZoomChanged()
        handleDecreaseZoomClicked()
        handleIncreaseZoomClicked()
        requestPermissions()
    }
    override fun onResume() {
        super.onResume()
        if (areAllPermissionsGranted()) {
            initZoomSeekBar()
            codeScanner.startPreview()
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CODE && areAllPermissionsGranted(grantResults)) {
            initZoomSeekBar()
            codeScanner.startPreview()
        }
    }
    override fun onBarcodeConfirmed(barcode: Barcode) {
        handleConfirmedBarcode(barcode)
    }
    override fun onBarcodeDeclined() {
        restartPreview()
    }
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        setLightStatusBar()
        disposable.clear()
    }
    private fun supportEdgeToEdge() {
        binding.imageViewFlash.applySystemWindowInsets(applyTop = true)
        binding.imageViewScanFromFile.applySystemWindowInsets(applyTop = true)
    }
    private fun setDarkStatusBar() {
        if (settings.isDarkTheme) {
            return
        }
        requireActivity().window.decorView.apply {
            systemUiVisibility = systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
    private fun setLightStatusBar() {
        if (settings.isDarkTheme) {
            return
        }
        requireActivity().window.decorView.apply {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
    private fun initScanner() {
        codeScanner = CodeScanner(requireActivity(), binding.scannerView).apply {
            camera = if (settings.isBackCamera) {
                CodeScanner.CAMERA_BACK
            } else {
                CodeScanner.CAMERA_FRONT
            }
            autoFocusMode = if (settings.simpleAutoFocus) {
                AutoFocusMode.SAFE
            } else {
                AutoFocusMode.CONTINUOUS
            }
            formats = SupportedBarcodeFormats.FORMATS.filter(settings::isFormatSelected)
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = settings.flash
            isTouchFocusEnabled = false
            decodeCallback = DecodeCallback(::handleScannedBarcode)
            errorCallback = ErrorCallback(::showError)
        }
    }
    private fun initZoomSeekBar() {
        scannerCameraHelper.getCameraParameters(settings.isBackCamera)?.apply {
            this@ScanBarcodeFromCameraFragment.maxZoom = maxZoom
            binding.seekBarZoom.max = maxZoom
            binding.seekBarZoom.progress = zoom
        }
    }
    private fun initFlashButton() {
        binding.layoutFlashContainer.setOnClickListener {
            toggleFlash()
        }
        binding.imageViewFlash.isActivated = settings.flash
    }
    private fun handleScanFromFileClicked() {
        binding.layoutScanFromFileContainer.setOnClickListener {
            navigateToScanFromFileScreen()
        }
    }
    private fun handleZoomChanged() {
        binding.seekBarZoom.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    codeScanner.zoom = progress
                }
            }
        })
    }
    private fun handleDecreaseZoomClicked() {
        binding.buttonDecreaseZoom.setOnClickListener {
            decreaseZoom()
        }
    }
    private fun handleIncreaseZoomClicked() {
        binding.buttonIncreaseZoom.setOnClickListener {
            increaseZoom()
        }
    }
    private fun decreaseZoom() {
        codeScanner.apply {
            if (zoom > zoomStep) {
                zoom -= zoomStep
            } else {
                zoom = 0
            }
            binding.seekBarZoom.progress = zoom
        }
    }
    private fun increaseZoom() {
        codeScanner.apply {
            if (zoom < maxZoom - zoomStep) {
                zoom += zoomStep
            } else {
                zoom = maxZoom
            }
           binding.seekBarZoom.progress = zoom
        }
    }
    private fun handleScannedBarcode(result: Result) {
        if (requireActivity().intent?.action == ZXING_SCAN_INTENT_ACTION) {
            vibrateIfNeeded()
            finishWithResult(result)
            return
        }
        if (settings.continuousScanning && result.equalTo(lastResult)) {
            restartPreviewWithDelay(false)
            return
        }
        vibrateIfNeeded()
        val barcode = barcodeParser.parseResult(result)
        when {
            settings.confirmScansManually -> showScanConfirmationDialog(barcode)
            settings.saveScannedBarcodesToHistory || settings.continuousScanning -> saveScannedBarcode(barcode)
            else -> navigateToBarcodeScreen(barcode)
        }
    }
    private fun handleConfirmedBarcode(barcode: Barcode) {
        when {
            settings.saveScannedBarcodesToHistory || settings.continuousScanning -> saveScannedBarcode(barcode)
            else -> navigateToBarcodeScreen(barcode)
        }
    }
    private fun vibrateIfNeeded() {
        if (settings.vibrate) {
            requireActivity().apply {
                runOnUiThread {
                    applicationContext.vibrator?.vibrateOnce(vibrationPattern)
                }
            }
        }
    }
    private fun showScanConfirmationDialog(barcode: Barcode) {
        val dialog = ConfirmBarcodeDialogFragment.newInstance(barcode)
        dialog.show(childFragmentManager, "")
    }
    private fun saveScannedBarcode(barcode: Barcode) {
        barcodeDatabase.save(barcode, settings.doNotSaveDuplicates)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    lastResult = barcode
                    when (settings.continuousScanning) {
                        true -> restartPreviewWithDelay(true)
                        else -> navigateToBarcodeScreen(barcode.copy(id = id))
                    }
                },
                ::showError
            )
            .addTo(disposable)
    }
    private fun restartPreviewWithDelay(showMessage: Boolean) {
        Completable
            .timer(CONTINUOUS_SCANNING_PREVIEW_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (showMessage) {
                    showToast(R.string.saved)
                }
                restartPreview()
            }
            .addTo(disposable)
    }
    private fun restartPreview() {
        requireActivity().runOnUiThread {
            codeScanner.startPreview()
        }
    }
    private fun toggleFlash() {
        binding.imageViewFlash.isActivated = binding.imageViewFlash.isActivated.not()
        codeScanner.isFlashEnabled = codeScanner.isFlashEnabled.not()
    }
    private fun showToast(stringId: Int) {
        toast?.cancel()
        toast = Toast.makeText(requireActivity(), stringId, Toast.LENGTH_SHORT).apply {
            show()
        }
    }
    private fun requestPermissions() {
        permissionsHelper.requestNotGrantedPermissions(requireActivity() as AppCompatActivity, PERMISSIONS, PERMISSION_REQUEST_CODE)
    }
    private fun areAllPermissionsGranted(): Boolean {
       return permissionsHelper.areAllPermissionsGranted(requireActivity(), PERMISSIONS)
    }
    private fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        return permissionsHelper.areAllPermissionsGranted(grantResults)
    }
    private fun navigateToScanFromFileScreen() {
        ScanBarcodeFromFileActivity.start(requireActivity())
    }
    private fun navigateToBarcodeScreen(barcode: Barcode) {
        BarcodeActivity.start(requireActivity(), barcode)
    }
    private fun finishWithResult(result: Result) {
        val intent = Intent()
            .putExtra("SCAN_RESULT", result.text)
            .putExtra("SCAN_RESULT_FORMAT", result.barcodeFormat.toString())
        if (result.rawBytes?.isNotEmpty() == true) {
            intent.putExtra("SCAN_RESULT_BYTES", result.rawBytes)
        }
        result.resultMetadata?.let { metadata ->
            metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
                intent.putExtra("SCAN_RESULT_ORIENTATION", it.toString())
            }
            metadata[ResultMetadataType.ERROR_CORRECTION_LEVEL]?.let {
                intent.putExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL", it.toString())
            }
            metadata[ResultMetadataType.UPC_EAN_EXTENSION]?.let {
                intent.putExtra("SCAN_RESULT_UPC_EAN_EXTENSION", it.toString())
            }
            metadata[ResultMetadataType.BYTE_SEGMENTS]?.let {
                @Suppress("UNCHECKED_CAST")
                for ((i, seg) in (it as Iterable<ByteArray>).withIndex()) {
                    intent.putExtra("SCAN_RESULT_BYTE_SEGMENTS_$i", seg)
                }
            }
        }
        requireActivity().apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}