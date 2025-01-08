package com.vungn.admob.manager

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.vungn.admob.util.AdMobConfig
import com.vungn.admob.util.Timer


class OpenAdManager private constructor(private val activity: Activity) {
    private var appOpenAd: AppOpenAd? = null
    private var _isShowingAd: Boolean = false
    private var _isLoadingAd: Boolean = false
    private var _loadingTimeout: Long = AdMobConfig.COUNTER_TIME_MILLISECONDS
    private var _listener: MutableList<OpenAdListener> = mutableListOf()
    private var _state: State = State.NONE
    private val _timer: Timer = Timer()

    init {
        _listener.add(object : OpenAdListener {
            override fun onStateChange(manager: OpenAdManager, state: State) {
                _state = state
            }
        })
    }

    private fun addListener(listener: OpenAdListener) {
        _listener.add(listener)
    }

    fun clearAllListener() {
        _listener.clear()
    }

    private fun setTimeout(timeout: Long) {
        _loadingTimeout = timeout
    }

    /** Request an ad */
    fun loadAd() {
        // We will implement this below.
        if (_isLoadingAd) {
            return
        }
        if (isAdAvailable()) {
            _listener.forEach { it.onStateChange(this, State.LOADED) }
            return
        }
        _isLoadingAd = true
        _listener.forEach { it.onStateChange(this, State.LOADING) }
        _timer.start(_loadingTimeout) {
            if (_state == State.LOADING) {
                _listener.forEach { it.onStateChange(this, State.TIME_OUT) }
            }
        }
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@OpenAdManager.appOpenAd = appOpenAd
                _isLoadingAd = false
                _listener.forEach { it.onStateChange(this@OpenAdManager, State.LOADED) }
                _timer.finish()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                _isLoadingAd = false
                Log.e(TAG, "onAdFailedToLoad: ${p0.message}")
                _listener.forEach { it.onStateChange(this@OpenAdManager, State.NOT_LOADED) }
                _timer.finish()
            }
        }

        val request: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        AppOpenAd.load(
            activity, AdMobConfig.APP_OPEN_AD_KEY, request, loadCallback
        )
    }

    fun showAd() {
        if (!isAdAvailable()) {
            _listener.forEach { it.onStateChange(this, State.NOT_LOADED) }
            return
        }
        if (_isShowingAd) {
            _listener.forEach { it.onStateChange(this, State.SHOWING) }
            return
        }
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                _isShowingAd = false
                _listener.forEach { it.onStateChange(this@OpenAdManager, State.CLOSED) }
            }


            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                _isShowingAd = false
                Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                _listener.forEach { it.onStateChange(this@OpenAdManager, State.CLOSED) }
            }

            override fun onAdShowedFullScreenContent() {
                _isShowingAd = true
                _listener.forEach { it.onStateChange(this@OpenAdManager, State.SHOWING) }
            }
        }
        appOpenAd?.show(activity)
    }

    fun release() {
        appOpenAd = null
    }

    /** Utility method that checks if ad exists and can be shown. */
    private fun isAdAvailable(): Boolean = appOpenAd != null

    interface OpenAdListener {
        fun onStateChange(manager: OpenAdManager, state: State)
    }

    enum class State {
        NONE, NOT_LOADED, SHOWING, LOADED, LOADING, CLOSED, TIME_OUT
    }

    class Builder(activity: Activity) {
        private val openAdManager = OpenAdManager(activity)

        fun setTimeout(timeout: Long): Builder {
            openAdManager.setTimeout(timeout)
            return this
        }

        fun addListener(listener: OpenAdListener): Builder {
            openAdManager.addListener(listener)
            return this
        }

        fun build(): OpenAdManager {
            return openAdManager
        }
    }

    companion object {
        private val TAG = OpenAdManager::class.simpleName
    }
}