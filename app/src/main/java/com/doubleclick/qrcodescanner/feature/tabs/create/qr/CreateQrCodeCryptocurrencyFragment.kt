package com.doubleclick.qrcodescanner.feature.tabs.create.qr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.doubleclick.qrcodescanner.R
import com.doubleclick.qrcodescanner.databinding.FragmentCreateQrCodeCryptocurrencyBinding
import com.doubleclick.qrcodescanner.extension.isNotBlank
import com.doubleclick.qrcodescanner.extension.textString
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.Cryptocurrency
import com.doubleclick.qrcodescanner.model.schema.Schema
class CreateQrCodeCryptocurrencyFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateQrCodeCryptocurrencyBinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateQrCodeCryptocurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCryptocurrenciesSpinner()
        initAddressEditText()
        handleTextChanged()
    }
    override fun getBarcodeSchema(): Schema {
        val cryptocurrency = when (binding.spinnerCryptocurrency.selectedItemPosition) {
            0 -> "bitcoin"
            1 -> "bitcoincash"
            2 -> "ethereum"
            3 -> "litecoin"
            4 -> "dash"
            else -> "bitcoin"
        }
        return Cryptocurrency(
            cryptocurrency = cryptocurrency,
            address = binding.editTextAddress.textString,
            label = binding.editTextLabel.textString,
            amount = binding.editTextAmount.textString,
            message = binding.editTextMessage.textString
        )
    }
    private fun initCryptocurrenciesSpinner() {
        binding.spinnerCryptocurrency.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.fragment_create_qr_code_cryptocurrencies, R.layout.item_spinner
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }
    private fun initAddressEditText() {
        binding.editTextAddress.requestFocus()
    }
    private fun handleTextChanged() {
        listOf(binding.editTextAddress, binding.editTextAmount, binding.editTextLabel, binding.editTextMessage).forEach { editText ->
            editText.addTextChangedListener { toggleCreateBarcodeButton() }
        }
    }
    private fun toggleCreateBarcodeButton() {
        parentActivity.isCreateBarcodeButtonEnabled = binding.editTextAddress.isNotBlank()
    }
}