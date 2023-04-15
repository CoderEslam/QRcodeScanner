package com.doubleclick.qrcodescanner.feature.tabs.create.qr
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.doubleclick.qrcodescanner.databinding.FragmentCreateQrCodeAppBinding
import com.doubleclick.qrcodescanner.extension.showError
import com.doubleclick.qrcodescanner.extension.unsafeLazy
import com.doubleclick.qrcodescanner.feature.tabs.create.BaseCreateBarcodeFragment
import com.doubleclick.qrcodescanner.model.schema.App
import com.doubleclick.qrcodescanner.model.schema.Schema
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
class CreateQrCodeAppFragment : BaseCreateBarcodeFragment() {
    private lateinit var _binding: FragmentCreateQrCodeAppBinding
    private val binding get() = _binding
    private val disposable = CompositeDisposable()
    private val appAdapter by unsafeLazy { AppAdapter(parentActivity) }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateQrCodeAppBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        loadApps()
    }
    override fun getBarcodeSchema(): Schema {
        return App.fromPackage("")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }
    private fun initRecyclerView() {
        binding.recyclerViewApps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appAdapter
        }
    }
    private fun loadApps() {
        showLoading(true)
        Single.just(getApps())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { apps ->
                    showLoading(false)
                    showApps(apps)
                },
                { error ->
                    showLoading(false)
                    showError(error)
                }
            )
            .addTo(disposable)
    }
    private fun getApps(): List<ResolveInfo> {
        val mainIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return requireContext().packageManager
            .queryIntentActivities(mainIntent, 0)
            .filter { it.activityInfo?.packageName != null }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLoading.isVisible = isLoading
        binding.recyclerViewApps.isVisible = isLoading.not()
    }
    private fun showApps(apps: List<ResolveInfo>) {
        appAdapter.apps = apps
    }
}