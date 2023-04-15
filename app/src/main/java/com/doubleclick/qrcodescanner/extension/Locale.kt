package com.doubleclick.qrcodescanner.extension
import java.util.Locale
val Locale?.isRussian: Boolean get() = this?.language == "ru"