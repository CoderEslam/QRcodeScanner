package com.doubleclick.qrcodescanner.feature.tabs.create.qr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.doubleclick.qrcodescanner.databinding.FragmentCreateQrCodeMmsBinding
import com.doubleclick.qrcodescanner.extension.isNotBlank
import com.doubleclick.qrcodescanner.extension.textString
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.Mms
import com.doubleclick.qrcodescanner.model.schema.Schema
class CreateQrCodeMmsFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateQrCodeMmsBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateQrCodeMmsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTitleEditText()
        handleTextChanged()
    }
    override fun showPhone(phone: String) {
        binding.editTextPhone.apply {
            setText(phone)
            setSelection(phone.length)
        }
    }
    override fun getBarcodeSchema(): Schema {
        return Mms(
            phone = binding.editTextPhone.textString,
            subject = binding.editTextSubject.textString,
            message = binding.editTextMessage.textString
        )
    }
    private fun initTitleEditText() {
        binding.editTextPhone.requestFocus()
    }
    private fun handleTextChanged() {
        binding.editTextPhone.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.editTextSubject.addTextChangedListener { toggleCreateBarcodeButton() }
        binding.editTextMessage.addTextChangedListener { toggleCreateBarcodeButton() }
    }
    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = binding.editTextPhone.isNotBlank() || binding.editTextSubject.isNotBlank() || binding.editTextMessage.isNotBlank()
    }
}