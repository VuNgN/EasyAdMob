package com.vungn.admob.manager

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.vungn.admob.util.AdModeConfig
import com.vungn.admob.util.Timer


class AppOpenAdManager private constructor(private val activity: Activity) {
    private var appOpenAd: AppOpenAd? = null
    private var _isShowingAd: Boolean = false
    private var _isLoadingAd: Boolean = false
    private var _loadingTimeout: Long = AdModeConfig.COUNTER_TIME_MILLISECONDS
    private var _listener: MutableList<OpenAdListener> = mutableListOf()
    private var _state: State = State.NONE
    private val _timer: Timer = Timer()

    init {
        _listener.add(object : OpenAdListener {
            override fun onStateChange(state: State) {
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
            _listener.forEach { it.onStateChange(State.LOADED) }
            return
        }
        _isLoadingAd = true
        _listener.forEach { it.onStateChange(State.LOADING) }
        _timer.start(_loadingTimeout) {
            if (_state == State.LOADING) {
                _listener.forEach { it.onStateChange(State.TIME_OUT) }
            }
        }
        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                super.onAdLoaded(appOpenAd)
                this@AppOpenAdManager.appOpenAd = appOpenAd
                _isLoadingAd = false
                _listener.forEach { it.onStateChange(State.LOADED) }
                _timer.finish()
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                _isLoadingAd = false
                Log.e(TAG, "onAdFailedToLoad: ${p0.message}")
                _listener.forEach { it.onStateChange(State.NOT_LOADED) }
                _timer.finish()
            }
        }

        val request: AdRequest = AdRequest.Builder().setHttpTimeoutMillis(5000).build()
        AppOpenAd.load(
            activity, AdModeConfig.APP_OPEN_AD_KEY, request, loadCallback
        )
    }

    fun showAd() {
        if (!isAdAvailable()) {
            _listener.forEach { it.onStateChange(State.NOT_LOADED) }
            return
        }
        if (_isShowingAd) {
            _listener.forEach { it.onStateChange(State.SHOWING) }
            return
        }
        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                _isShowingAd = false
                _listener.forEach { it.onStateChange(State.CLOSED) }
            }


            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                _isShowingAd = false
                Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                _listener.forEach { it.onStateChange(State.CLOSED) }
            }

            override fun onAdShowedFullScreenContent() {
                _isShowingAd = true
                _listener.forEach { it.onStateChange(State.SHOWING) }
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
        fun onStateChange(state: State)
    }

    enum class State {
        NONE, NOT_LOADED, SHOWING, LOADED, LOADING, CLOSED, TIME_OUT
    }

    class Builder(activity: Activity) {
        private val appOpenAdManager = AppOpenAdManager(activity)

        fun setTimeout(timeout: Long): Builder {
            appOpenAdManager.setTimeout(timeout)
            return this
        }

        fun addListener(listener: OpenAdListener): Builder {
            appOpenAdManager.addListener(listener)
            return this
        }

        fun build(): AppOpenAdManager {
            return appOpenAdManager
        }
    }

    companion object {
        private val TAG = AppOpenAdManager::class.simpleName
    }
}