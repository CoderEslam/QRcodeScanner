package com.doubleclick.qrcodescanner
import androidx.multidex.MultiDexApplication
import com.doubleclick.qrcodescanner.di.settings
import com.google.firebase.FirebaseApp

class App : MultiDexApplication() {
    override fun onCreate() {
        applyTheme()
        FirebaseApp.initializeApp(this);
        super.onCreate()
    }
    private fun applyTheme() {
        settings.reapplyTheme()
    }
}