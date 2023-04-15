package com.doubleclick.qrcodescanner.feature.tabs.create.qr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.doubleclick.qrcodescanner.databinding.FragmentCreateQrCodeUrlBinding
import com.doubleclick.qrcodescanner.extension.isNotBlank
import com.doubleclick.qrcodescanner.extension.textString
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.Schema
import com.doubleclick.qrcodescanner.model.schema.Url
class CreateQrCodeUrlFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateQrCodeUrlBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateQrCodeUrlBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUrlPrefix()
        handleTextChanged()
    }
    override fun getBarcodeSchema(): Schema {
        return Url(binding.editText.textString)
    }
    private fun showUrlPrefix() {
        val prefix = "https://"
        binding.editText.apply {
            setText(prefix)
            setSelection(prefix.length)
            requestFocus()
        }
    }
    private fun handleTextChanged() {
        binding.editText.addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = binding.editText.isNotBlank()
        }
    }
}