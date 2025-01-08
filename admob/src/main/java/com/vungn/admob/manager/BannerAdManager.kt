package com.vungn.admob.manager

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowMetrics
import android.widget.FrameLayout
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest.Builder
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.vungn.admob.util.AdMobConfig
import com.vungn.admob.view.AdView
import java.util.UUID

class BannerAdManager(private val activity: Activity) : LifecycleEventObserver {
    private lateinit var adSize: AdSize
    private var adView: AdView? = null

    @RequiresPermission("android.permission.INTERNET")
    fun loadAd(
        adViewContainer: FrameLayout,
        lifecycle: Lifecycle,
        isCollapse: Boolean = false,
        listener: BannerAdLoadListener = object : BannerAdLoadListener() {},
    ) {
        val adView = AdView(activity)
        this.adView = adView
        adView.adUnitId = AdMobConfig.APP_BANNER_AD_KEY
        adSize = activity.let {
            val displayMetrics = it.resources.displayMetrics
            val adWidthPixels = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics: WindowMetrics = it.windowManager.currentWindowMetrics
                windowMetrics.bounds.width()
            } else {
                displayMetrics.widthPixels
            }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(it, adWidth)
        }
        adView.setAdSize(adSize)
        adViewContainer.addView(adView)
        val extras = Bundle()
        if (isCollapse) {
            extras.putString("collapsible", "bottom")
            extras.putString("collapsible_request_id", UUID.randomUUID().toString())
        }

        // Create an ad request.
        val adRequest = Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()

        adView.adListener = getListener(listener)

        // Start loading the ad in the background.
        adView.loadAd(adRequest)

        lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> adView?.onPause()
            Lifecycle.Event.ON_RESUME -> adView?.onResume()
            Lifecycle.Event.ON_DESTROY -> adView?.onDestroy()
            else -> {}
        }
    }

    private fun getListener(listener: BannerAdLoadListener): AdListener {
        return object : AdListener() {
            override fun onAdClicked() {
                listener.onAdClicked()
            }

            override fun onAdImpression() {
                listener.onAdImpression()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                listener.onAdFailedToLoad()
            }

            override fun onAdClosed() {
                listener.onAdClosed()
            }

            override fun onAdOpened() {
                listener.onAdOpened()
            }

            override fun onAdLoaded() {
                listener.onAdLoaded()
            }

            override fun onAdSwipeGestureClicked() {
                listener.onAdSwipeGestureClicked()
            }
        }
    }

    abstract class BannerAdLoadListener {
        open fun onAdClicked() {}
        open fun onAdImpression() {}
        open fun onAdFailedToLoad() {}
        open fun onAdClosed() {}
        open fun onAdLoaded() {}
        open fun onAdOpened() {}
        open fun onAdSwipeGestureClicked() {}
    }
}