package com.vungn.admob.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.nativead.NativeAdView
import com.vungn.admob.util.NativeAd

class NativeAdView : FrameLayout {
    private val _nativeAdView: NativeAdView
    private var _mediaView: MediaView? = null

    constructor(context: Context) : super(context) {
        _nativeAdView = NativeAdView(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        _nativeAdView = NativeAdView(context, attrs)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        _nativeAdView = NativeAdView(context, attrs, defStyleAttr)
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs, defStyleAttr, defStyleRes
    ) {
        _nativeAdView = NativeAdView(context, attrs, defStyleAttr, defStyleRes)
        init()
    }

    private fun init() {
        addView(_nativeAdView)
    }

    fun setHeadlineView(view: View) {
        _nativeAdView.headlineView = view
    }

    fun setBodyView(view: View) {
        _nativeAdView.bodyView = view
    }

    fun setCallToActionView(view: View) {
        _nativeAdView.callToActionView = view
    }

    fun setIconView(view: View) {
        _nativeAdView.iconView = view
    }

    fun setMediaView(view: MediaView) {
        _mediaView = view
        _nativeAdView.mediaView = view.mediaView
    }

    fun setPriceView(view: View) {
        _nativeAdView.priceView = view
    }

    fun setStarRatingView(view: View) {
        _nativeAdView.starRatingView = view
    }

    fun setStoreView(view: View) {
        _nativeAdView.storeView = view
    }

    fun setNativeAd(nativeAd: NativeAd) {
        _nativeAdView.setNativeAd(nativeAd.getNativeAd())
    }

    fun setAdvertiserView(findViewById: View) {
        _nativeAdView.advertiserView = findViewById
    }

    fun getHeadlineView(): View? {
        return _nativeAdView.headlineView
    }

    fun getMediaView(): MediaView? {
        return _mediaView
    }

    fun getBodyView(): View? {
        return _nativeAdView.bodyView
    }

    fun getCallToActionView(): View? {
        return _nativeAdView.callToActionView
    }

    fun getIconView(): View? {
        return _nativeAdView.iconView
    }

    fun getPriceView(): View? {
        return _nativeAdView.priceView
    }

    fun getStarRatingView(): View? {
        return _nativeAdView.starRatingView
    }

    fun getStoreView(): View? {
        return _nativeAdView.storeView
    }

    fun getAdvertiserView(): View? {
        return _nativeAdView.advertiserView
    }
}