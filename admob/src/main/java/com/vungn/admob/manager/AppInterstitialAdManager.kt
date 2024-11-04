package com.vungn.admob.manager

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vungn.admob.util.AdModeConfig
import com.vungn.admob.util.AdModeConfig.COUNTER_TIME_MILLISECONDS
import com.vungn.admob.util.Timer

class AppInterstitialAdManager private constructor(private val activity: Activity) {
    private var interstitialAd: InterstitialAd? = null
    private var adIsLoading: Boolean = false
    private var _loadingTimeout: Long = COUNTER_TIME_MILLISECONDS
    private var _state: State = State.NONE
    private var _listener: MutableList<InterstitialAdListener> = mutableListOf()
    private var timer: Timer = Timer()

    init {
        _listener.add(object : InterstitialAdListener {
            override fun onStateChange(state: State) {
                _state = state
            }
        })
    }

    private fun addListener(listener: InterstitialAdListener) {
        _listener.add(listener)
    }

    fun clearAllListener() {
        _listener.clear()
    }

    private fun setTimeout(timeout: Long) {
        _loadingTimeout = timeout
    }

    fun loadAd() {
        if (adIsLoading) {
            return
        }
        if (interstitialAd != null) {
            _listener.forEach { it.onStateChange(State.LOADED) }
            return
        }
        adIsLoading = true
        _listener.forEach { it.onStateChange(State.LOADING) }
        timer.start(_loadingTimeout) {
            if (_state == State.LOADING) {
                _listener.forEach { it.onStateChange(State.TIME_OUT) }
            }
        }

        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(activity,
            AdModeConfig.APP_INTERSTITIAL_AD_KEY,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    interstitialAd = null
                    adIsLoading = false
                    val error =
                        "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
                    Log.e(TAG, error)
                    _listener.forEach { it.onStateChange(State.NOT_LOADED) }
                    timer.finish()
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    interstitialAd = ad
                    adIsLoading = false
                    _listener.forEach { it.onStateChange(State.LOADED) }
                    timer.finish()
                }
            })
    }

    fun showAd() {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    _listener.forEach { it.onStateChange(State.CLOSED) }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    interstitialAd = null
                    _listener.forEach { it.onStateChange(State.CLOSED) }
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    _listener.forEach { it.onStateChange(State.SHOWING) }
                    // Called when ad is dismissed.
                }
            }
            interstitialAd?.show(activity)
        } else {
            _listener.forEach { it.onStateChange(State.NOT_LOADED) }
        }
    }

    interface InterstitialAdListener {
        fun onStateChange(state: State)
    }

    enum class State {
        NONE, TIME_OUT, NOT_LOADED, LOADING, LOADED, SHOWING, CLOSED
    }

    class Builder(activity: Activity) {
        private val appInterstitialAdManager = AppInterstitialAdManager(activity)

        fun addListener(listener: InterstitialAdListener): Builder {
            appInterstitialAdManager.addListener(listener)
            return this
        }

        fun setTimeout(timeout: Long): Builder {
            appInterstitialAdManager.setTimeout(timeout)
            return this
        }

        fun build(): AppInterstitialAdManager {
            return appInterstitialAdManager
        }
    }

    companion object {
        private val TAG = AppInterstitialAdManager::class.simpleName
    }
}