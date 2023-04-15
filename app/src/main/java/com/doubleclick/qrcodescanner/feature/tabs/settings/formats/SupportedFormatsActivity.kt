package com.doubleclick.qrcodescanner.feature.tabs.settings.formats
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubleclick.qrcodescanner.databinding.ActivitySupportedFormatsBinding
import com.doubleclick.qrcodescanner.di.settings
import com.doubleclick.qrcodescanner.extension.applySystemWindowInsets
import com.doubleclick.qrcodescanner.extension.unsafeLazy
import com.doubleclick.qrcodescanner.feature.BaseActivity
import com.doubleclick.qrcodescanner.usecase.SupportedBarcodeFormats
import com.google.zxing.BarcodeFormat
class SupportedFormatsActivity : BaseActivity(), FormatsAdapter.Listener {
    private lateinit var binding: ActivitySupportedFormatsBinding
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, SupportedFormatsActivity::class.java)
            context.startActivity(intent)
        }
    }
    private val formats by unsafeLazy { SupportedBarcodeFormats.FORMATS }
    private val formatSelection by unsafeLazy { formats.map(settings::isFormatSelected) }
    private val formatsAdapter by unsafeLazy { FormatsAdapter(this, formats, formatSelection) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportedFormatsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportEdgeToEdge()
        initRecyclerView()
        handleToolbarBackClicked()
    }
    override fun onFormatChecked(format: BarcodeFormat, isChecked: Boolean) {
        settings.setFormatSelected(format, isChecked)
    }
    private fun supportEdgeToEdge() {
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = true)
    }
    private fun initRecyclerView() {
        binding.recyclerViewFormats.apply {
            layoutManager = LinearLayoutManager(this@SupportedFormatsActivity)
            adapter = formatsAdapter
        }
    }
    private fun handleToolbarBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}