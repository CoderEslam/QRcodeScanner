package com.doubleclick.qrcodescanner.extension
fun Int?.orZero(): Int {
    return this ?: 0
}