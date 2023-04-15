package com.doubleclick.qrcodescanner.di
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.doubleclick.qrcodescanner.App
import com.doubleclick.qrcodescanner.usecase.BarcodeImageScanner
import com.doubleclick.qrcodescanner.usecase.BarcodeSaver
import com.doubleclick.qrcodescanner.usecase.BarcodeImageGenerator
import com.doubleclick.qrcodescanner.usecase.BarcodeImageSaver
import com.doubleclick.qrcodescanner.usecase.BarcodeParser
import com.doubleclick.qrcodescanner.usecase.WifiConnector
import com.doubleclick.qrcodescanner.usecase.OTPGenerator
import com.doubleclick.qrcodescanner.usecase.Settings
import com.doubleclick.qrcodescanner.usecase.BarcodeDatabase
import com.doubleclick.qrcodescanner.usecase.ContactHelper
import com.doubleclick.qrcodescanner.usecase.PermissionsHelper
import com.doubleclick.qrcodescanner.usecase.RotationHelper
import com.doubleclick.qrcodescanner.usecase.ScannerCameraHelper
val App.settings get() = Settings.getInstance(applicationContext)
val barcodeParser get() = BarcodeParser
val barcodeImageScanner get() = BarcodeImageScanner
val barcodeImageGenerator get() = BarcodeImageGenerator
val barcodeSaver get() = BarcodeSaver
val barcodeImageSaver get() = BarcodeImageSaver
val wifiConnector get() = WifiConnector
val otpGenerator get() = OTPGenerator
val AppCompatActivity.barcodeDatabase get() = BarcodeDatabase.getInstance(this)
val AppCompatActivity.settings get() = Settings.getInstance(this)
val contactHelper get() = ContactHelper
val permissionsHelper get() = PermissionsHelper
val rotationHelper get() = RotationHelper
val scannerCameraHelper get() = ScannerCameraHelper
val Fragment.barcodeParser get() = BarcodeParser
val Fragment.barcodeDatabase get() = BarcodeDatabase.getInstance(requireContext())
val Fragment.settings get() = Settings.getInstance(requireContext())
val Fragment.permissionsHelper get() = PermissionsHelper