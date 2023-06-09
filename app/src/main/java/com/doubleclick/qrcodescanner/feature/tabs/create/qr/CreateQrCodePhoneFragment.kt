package com.doubleclick.qrcodescanner.feature.tabs.create.qr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.doubleclick.qrcodescanner.databinding.FragmentCreateQrCodePhoneBinding
import com.doubleclick.qrcodescanner.extension.isNotBlank
import com.doubleclick.qrcodescanner.extension.textString
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.Phone
import com.doubleclick.qrcodescanner.model.schema.Schema
class CreateQrCodePhoneFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateQrCodePhoneBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateQrCodePhoneBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditText()
        handleTextChanged()
    }
    override fun showPhone(phone: String) {
        binding.editText.apply {
            setText(phone)
            setSelection(phone.length)
        }
    }
    override fun getBarcodeSchema(): Schema {
        return Phone(binding.editText.textString)
    }
    private fun initEditText() {
        binding.editText.requestFocus()
    }
    private fun handleTextChanged() {
        binding.editText.addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = binding.editText.isNotBlank()
        }
    }
}