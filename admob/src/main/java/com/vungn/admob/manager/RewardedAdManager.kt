package com.vungn.admob.manager

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.vungn.admob.util.AdMobConfig
import com.vungn.admob.util.RewardItem
import com.vungn.admob.util.Timer

class RewardedAdManager private constructor(private val activity: Activity) {
    private var rewardedAd: RewardedAd? = null
    private var _isLoading = false
    private var _isShowing = false
    private var _loadingTimeout: Long = AdMobConfig.COUNTER_TIME_MILLISECONDS
    private var _listener: MutableList<AppRewardedAdListener> = mutableListOf()
    private var _state: State = State.NONE
    private val _timer: Timer = Timer()
    private var _rewardItem: RewardItem? = null

    val rewardItem: RewardItem?
        get() = _rewardItem

    fun addListener(listener: AppRewardedAdListener) {
        _listener.add(listener)
    }

    fun clearAllListener() {
        _listener.clear()
    }

    private fun setTimeout(timeout: Long) {
        _loadingTimeout = timeout
    }

    init {
        _listener.add(object : AppRewardedAdListener {
            override fun onStateChange(state: State) {
                _state = state
            }

            override fun onAdClicked() {
            }

            override fun onUserEarnedReward(rewardItem: RewardItem) {
            }
        })
    }

    fun loadAd() {
        if (_isLoading) {
            return
        }
        if (rewardedAd != null) {
            _listener.forEach { it.onStateChange(State.LOADED) }
            return
        }
        _isLoading = true
        _listener.forEach { it.onStateChange(State.LOADING) }
        _timer.start(_loadingTimeout) {
            if (_state == State.LOADING) {
                _listener.forEach { it.onStateChange(State.TIME_OUT) }
            }
        }
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(activity,
            AdMobConfig.APP_REWARDED_AD_KEY,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "Ad failed to load")
                    rewardedAd = null
                    _isLoading = false
                    _listener.forEach { it.onStateChange(State.NOT_LOADED) }
                    _timer.finish()
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Ad loaded")
                    rewardedAd = ad
                    _rewardItem = RewardItem(ad.rewardItem)
                    _isLoading = false
                    _listener.forEach { it.onStateChange(State.LOADED) }
                    _timer.finish()
                }
            })
    }

    fun showAd() {
        if (_isShowing) {
            _listener.forEach { it.onStateChange(State.SHOWING) }
            return
        }
        if (rewardedAd == null) {
            _listener.forEach { it.onStateChange(State.NOT_LOADED) }
            return
        }
        _isShowing = true
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
                _listener.forEach { it.onAdClicked() }
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
                _isShowing = false
                _listener.forEach { it.onStateChange(State.CLOSED) }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
                _isShowing = false
                _listener.forEach { it.onStateChange(State.CLOSED) }
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
                _isShowing = true
                _listener.forEach { it.onStateChange(State.SHOWING) }
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
                _isShowing = true
                _listener.forEach {
                    it.onStateChange(State.SHOWING)
                }
            }
        }
        rewardedAd?.show(activity) { rewardItem ->
            _rewardItem = RewardItem(rewardItem)
            Log.d(TAG, "User earned the reward.")
            _isShowing = false
            _listener.forEach { it.onUserEarnedReward(RewardItem(rewardItem)) }
        }
    }

    fun release() {
        rewardedAd = null
    }

    interface AppRewardedAdListener {
        fun onStateChange(state: State)
        fun onAdClicked()
        fun onUserEarnedReward(rewardItem: RewardItem)
    }

    enum class State {
        NONE, LOADED, NOT_LOADED, LOADING, SHOWING, CLOSED, TIME_OUT
    }

    class Builder(activity: Activity) {
        private val rewardedAdManager = RewardedAdManager(activity)

        fun setTimeout(timeout: Long): Builder {
            rewardedAdManager.setTimeout(timeout)
            return this
        }

        fun addListener(listener: AppRewardedAdListener): Builder {
            rewardedAdManager.addListener(listener)
            return this
        }

        fun build(): RewardedAdManager {
            return rewardedAdManager
        }
    }

    companion object {
        private val TAG = this::class.simpleName
    }
}