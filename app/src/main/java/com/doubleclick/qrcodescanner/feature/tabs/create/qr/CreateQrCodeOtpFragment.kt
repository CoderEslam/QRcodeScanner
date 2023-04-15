package com.doubleclick.qrcodescanner.feature.tabs.create.qr
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.doubleclick.qrcodescanner.R
import com.doubleclick.qrcodescanner.databinding.FragmentCreateQrCodeOtpBinding
import com.doubleclick.qrcodescanner.extension.encodeBase32
import com.doubleclick.qrcodescanner.extension.isNotBlank
import com.doubleclick.qrcodescanner.extension.textString
import com.doubleclick.qrcodescanner.extension.toHmacAlgorithm
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.OtpAuth
import com.doubleclick.qrcodescanner.model.schema.Schema
import dev.turingcomplete.kotlinonetimepassword.RandomSecretGenerator
import java.util.Locale
class CreateQrCodeOtpFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateQrCodeOtpBinding
    private val binding get() = _binding
    private val randomGenerator = RandomSecretGenerator()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateQrCodeOtpBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOtpTypesSpinner()
        initAlgorithmsSpinner()
        initEditTexts()
        initGenerateRandomSecretButton()
        showRandomSecret()
    }
    override fun getBarcodeSchema(): Schema {
        return OtpAuth(
            type = binding.spinnerOptTypes.selectedItem?.toString()?.lowercase(Locale.ENGLISH),
            algorithm = binding.spinnerAlgorithms.selectedItem?.toString(),
            label = if (binding.editTextIssuer.isNotBlank()) {
                "${binding.editTextIssuer.textString}:${binding.editTextAccount.textString}"
            } else {
                binding.editTextAccount.textString
            },
            issuer = binding.editTextIssuer.textString,
            digits = binding.editTextDigits.textString.toIntOrNull(),
            period = binding.editTextPeriod.textString.toLongOrNull(),
            counter = binding.editTextCounter.textString.toLongOrNull(),
            secret = binding.editTextSecret.textString)
    }
    private fun initOtpTypesSpinner() {
        binding.spinnerOptTypes.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.fragment_create_qr_code_otp_types, R.layout.item_spinner).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
        binding.spinnerOptTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.textInputLayoutCounter.isVisible = position == 0
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
    private fun initAlgorithmsSpinner() {
        binding.spinnerAlgorithms.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.fragment_create_qr_code_otp_algorithms, R.layout.item_spinner).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }
    private fun initEditTexts() {
        binding.editTextAccount.addTextChangedListener {
            toggleCreateBarcodeButton()
        }
        binding.editTextSecret.addTextChangedListener {
            toggleCreateBarcodeButton()
        }
        binding.editTextPeriod.addTextChangedListener {
            toggleCreateBarcodeButton()
        }
        binding.editTextCounter.addTextChangedListener {
            toggleCreateBarcodeButton()
        }
    }
    private fun initGenerateRandomSecretButton() {
        binding.buttonGenerateRandomSecret.setOnClickListener {
            showRandomSecret()
        }
    }
    private fun toggleCreateBarcodeButton() {
        val isHotp = binding.spinnerOptTypes.selectedItemPosition == 0
        val areGeneralFieldsNotBlank = binding.editTextAccount.isNotBlank() && binding.editTextSecret.isNotBlank()
        val areHotpFieldsNotBlank = binding.editTextCounter.isNotBlank() && binding.editTextPeriod.isNotBlank()
        parentActivity.isCreateBarcodeButtonEnabled = areGeneralFieldsNotBlank && (isHotp.not() || isHotp && areHotpFieldsNotBlank)
    }
    private fun showRandomSecret() {
        binding.editTextSecret.setText(generateRandomSecret())
    }
    private fun generateRandomSecret(): String {
        val algorithm = binding.spinnerAlgorithms.selectedItem?.toString().toHmacAlgorithm()
        val secret = randomGenerator.createRandomSecret(algorithm)
        return secret.encodeBase32()
    }
}