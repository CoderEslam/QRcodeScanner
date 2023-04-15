package com.doubleclick.qrcodescanner.extension
import com.doubleclick.qrcodescanner.model.Barcode
import com.google.zxing.Result
fun Result.equalTo(barcode: Barcode?): Boolean {
    return barcodeFormat == barcode?.format && text == barcode?.text
}