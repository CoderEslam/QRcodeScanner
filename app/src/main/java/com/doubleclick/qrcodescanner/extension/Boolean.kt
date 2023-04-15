package com.doubleclick.qrcodescanner.extension
fun Boolean?.orFalse(): Boolean {
    return this ?: false
}