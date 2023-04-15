package com.doubleclick.qrcodescanner.extension
fun Double?.orZero(): Double {
    return this ?: 0.0
}