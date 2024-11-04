package com.vungn.admob.util

import android.graphics.drawable.Drawable
import android.net.Uri
import com.google.android.gms.ads.nativead.NativeAd

class NativeAd(private val nativeAd: NativeAd) {
    private var _mediaContent: MediaContent = MediaContent(nativeAd.mediaContent)
    private var _image: Image = Image(nativeAd.icon)

    fun getNativeAd(): NativeAd {
        return nativeAd
    }

    fun destroy() {
        nativeAd.destroy()
    }

    fun getHeadline(): String? {
        return nativeAd.headline
    }

    fun getMediaContent(): MediaContent? {
        return _mediaContent
    }

    fun getBody(): String? {
        return nativeAd.body
    }

    fun getCallToAction(): String? {
        return nativeAd.callToAction
    }

    fun getIcon(): Image? {
        return _image
    }

    fun getPrice(): String? {
        return nativeAd.price
    }

    fun getStore(): String? {
        return nativeAd.store
    }

    fun getStarRating(): Double? {
        return nativeAd.starRating
    }

    fun getAdvertiser(): String? {
        return nativeAd.advertiser
    }

    companion object {
        class Image(private val image: NativeAd.Image?) {
            fun getDrawable(): Drawable? = image?.drawable
            fun getUrl(): Uri? = image?.uri
            fun getScale(): Double? = image?.scale
        }
    }
}