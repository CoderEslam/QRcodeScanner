package com.doubleclick.qrcodescanner.feature.tabs.settings.permissions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.doubleclick.qrcodescanner.databinding.ActivityAllPermissionsBinding
import com.doubleclick.qrcodescanner.extension.applySystemWindowInsets
import com.doubleclick.qrcodescanner.feature.BaseActivity
import me.zhanghai.android.fastscroll.FastScrollerBuilder
class AllPermissionsActivity : BaseActivity() {
    private lateinit var binding: ActivityAllPermissionsBinding
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AllPermissionsActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FastScrollerBuilder(binding.scrollView).useMd2Style().build()
        binding.rootView.applySystemWindowInsets(applyTop = true, applyBottom = true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}