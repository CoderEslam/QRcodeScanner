package com.doubleclick.qrcodescanner.extension
import androidx.appcompat.app.AppCompatActivity
import com.doubleclick.qrcodescanner.feature.common.dialog.ErrorDialogFragment
fun AppCompatActivity.showError(error: Throwable?) {
    val errorDialog = ErrorDialogFragment.newInstance(this, error)
    errorDialog.show(supportFragmentManager, "")
}