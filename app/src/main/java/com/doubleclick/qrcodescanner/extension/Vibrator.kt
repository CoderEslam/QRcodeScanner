package com.doubleclick.qrcodescanner.extension
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
fun Vibrator.vibrateOnce(pattern: LongArray) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(VibrationEffect.createWaveform(pattern, -1))
    } else {
        @Suppress("DEPRECATION")
        vibrate(pattern, -1)
    }
}