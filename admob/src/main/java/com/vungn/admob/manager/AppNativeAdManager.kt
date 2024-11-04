package com.vungn.admob.manager

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresPermission
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.vungn.admob.util.AdModeConfig
import com.vungn.admob.util.NativeAd

class AppNativeAdManager(private val activity: Activity) {
    private var nativeAd: NativeAd? = null

    @RequiresPermission("android.permission.INTERNET")
    fun loadAd(
        videoMuted: Boolean = true, listener: NativeAdLoadListener
    ) {
        val builder = AdLoader.Builder(activity, AdModeConfig.APP_NATIVE_AD_KEY)

        builder.forNativeAd { nativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            val activityDestroyed: Boolean =
                Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || activity.isDestroyed
            if (activityDestroyed || activity.isFinishing || activity.isChangingConfigurations) {
                nativeAd.destroy()
                return@forNativeAd
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (this.nativeAd != null) {
                this.nativeAd?.destroy()
            }
            listener.onAdLoaded(NativeAd(nativeAd))
        }

        val videoOptions = VideoOptions.Builder().setStartMuted(videoMuted).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(getListener(listener)).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun getListener(listener: NativeAdLoadListener): AdListener {
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

            override fun onAdSwipeGestureClicked() {
                listener.onAdSwipeGestureClicked()
            }
        }
    }

    abstract class NativeAdLoadListener {
        open fun onAdLoaded(currentNativeAd: NativeAd) {}
        open fun onAdClicked() {}
        open fun onAdImpression() {}
        open fun onAdFailedToLoad() {}
        open fun onAdClosed() {}
        open fun onAdOpened() {}
        open fun onAdSwipeGestureClicked() {}
    }
}