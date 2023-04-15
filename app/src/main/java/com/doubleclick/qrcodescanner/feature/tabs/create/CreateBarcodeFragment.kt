package com.doubleclick.qrcodescanner.feature.tabs.create
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.doubleclick.qrcodescanner.databinding.FragmentCreateBarcodeBinding
import com.doubleclick.qrcodescanner.extension.applySystemWindowInsets
import com.doubleclick.qrcodescanner.extension.clipboardManager
import com.doubleclick.qrcodescanner.extension.orZero
import com.doubleclick.qrcodescanner.feature.tabs.create.barcode.CreateBarcodeAllActivity
import com.doubleclick.qrcodescanner.feature.tabs.create.qr.CreateQrCodeAllActivity
import com.doubleclick.qrcodescanner.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class CreateBarcodeFragment : Fragment() {
    private lateinit var _binding: FragmentCreateBarcodeBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateBarcodeBinding.inflate(inflater, container, false)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportEdgeToEdge()
        handleButtonsClicked()
    }
    private fun supportEdgeToEdge() {
        binding.appBarLayout.applySystemWindowInsets(applyTop = true)
    }
    private fun handleButtonsClicked() {
        binding.buttonClipboard.setOnClickListener {
            CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER, getClipboardContent())
        }
        binding.buttonText.setOnClickListener {
            CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.OTHER)
        }
        binding.buttonUrl.setOnClickListener {
            CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.URL)
        }
        binding.buttonWifi.setOnClickListener {
            CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.WIFI)
        }
        binding.buttonLocation.setOnClickListener {
            CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.GEO)
        }
        binding.buttonContactVcard.setOnClickListener {
            CreateBarcodeActivity.start(requireActivity(), BarcodeFormat.QR_CODE, BarcodeSchema.VCARD)
        }
        binding.buttonShowAllQrCode.setOnClickListener {
            CreateQrCodeAllActivity.start(requireActivity())
        }
        binding.buttonCreateBarcode.setOnClickListener {
            CreateBarcodeAllActivity.start(requireActivity())
        }
    }
    private fun getClipboardContent(): String {
        val clip = requireActivity().clipboardManager?.primaryClip ?: return ""
        return when (clip.itemCount.orZero()) {
            0 -> ""
            else -> clip.getItemAt(0).text.toString()
        }
    }
}