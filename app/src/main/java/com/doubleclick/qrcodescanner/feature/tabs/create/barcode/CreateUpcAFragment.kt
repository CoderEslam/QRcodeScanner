package com.doubleclick.qrcodescanner.feature.tabs.create.barcode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.doubleclick.qrcodescanner.databinding.FragmentCreateUpcABinding
import com.doubleclick.qrcodescanner.extension.textString
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.Other
import com.doubleclick.qrcodescanner.model.schema.Schema
class CreateUpcAFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateUpcABinding
    private val binding get() = _binding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding =  FragmentCreateUpcABinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editText.requestFocus()
        binding.editText.addTextChangedListener {
            parentActivity.isCreateBarcodeButtonEnabled = binding.editText.text!!.length == 11
        }
    }
    override fun getBarcodeSchema(): Schema {
        return Other(binding.editText.textString)
    }
}