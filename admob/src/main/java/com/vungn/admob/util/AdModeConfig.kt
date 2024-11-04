package com.vungn.admob.util

import android.content.res.Resources
import android.util.JsonReader
import androidx.annotation.RawRes
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

object AdModeConfig {
    const val COUNTER_TIME_MILLISECONDS = 3000L
    var APP_BANNER_AD_KEY = "ca-app-pub-3940256099942544/9214589741"
    var APP_INTERSTITIAL_AD_KEY = "ca-app-pub-3940256099942544/8691691433"
    var APP_REWARDED_AD_KEY = "ca-app-pub-3940256099942544/5224354917"
    var APP_NATIVE_AD_KEY = "ca-app-pub-3940256099942544/2247696110"
    var APP_OPEN_AD_KEY = "ca-app-pub-3940256099942544/9257395921"

    fun initAds(
        testDeviceIds: List<String> = emptyList(),
        maxAdContentRating: String? = null,
        publisherPrivacyPersonalizationState: PublisherPrivacyPersonalizationState = PublisherPrivacyPersonalizationState.DEFAULT,
        tagForChildDirectedTreatment: Int = -1,
        tagForUnderAgeOfConsent: Int = -1
    ) {
        val ppps = when (publisherPrivacyPersonalizationState) {
            PublisherPrivacyPersonalizationState.DEFAULT -> RequestConfiguration.PublisherPrivacyPersonalizationState.DEFAULT
            PublisherPrivacyPersonalizationState.ENABLED -> RequestConfiguration.PublisherPrivacyPersonalizationState.ENABLED
            PublisherPrivacyPersonalizationState.DISABLED -> RequestConfiguration.PublisherPrivacyPersonalizationState.DISABLED
        }
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setMaxAdContentRating(maxAdContentRating)
                .setPublisherPrivacyPersonalizationState(ppps)
                .setTagForChildDirectedTreatment(tagForChildDirectedTreatment)
                .setTagForUnderAgeOfConsent(tagForUnderAgeOfConsent).setTestDeviceIds(testDeviceIds)
                .build()
        )
    }

    fun loadKeys(@RawRes rawRes: Int, resources: Resources) {
        // Load keys from raw resource
        resources.openRawResource(rawRes).bufferedReader().use {
            val reader = JsonReader(it)
            reader.beginObject()
            while (reader.hasNext()) {
                val key = reader.nextName()
                val value = reader.nextString()
                when (key) {
                    "APP_BANNER_AD_KEY" -> APP_BANNER_AD_KEY = value
                    "APP_INTERSTITIAL_AD_KEY" -> APP_INTERSTITIAL_AD_KEY = value
                    "APP_REWARDED_AD_KEY" -> APP_REWARDED_AD_KEY = value
                    "APP_NATIVE_AD_KEY" -> APP_NATIVE_AD_KEY = value
                    "APP_OPEN_AD_KEY" -> APP_OPEN_AD_KEY = value
                }
            }
            reader.endObject()
        }
    }

    fun loadKeys(
        bannerKey: String,
        interstitialKey: String,
        rewardedKey: String,
        nativeKey: String,
        openKey: String
    ) {
        APP_BANNER_AD_KEY = bannerKey
        APP_INTERSTITIAL_AD_KEY = interstitialKey
        APP_REWARDED_AD_KEY = rewardedKey
        APP_NATIVE_AD_KEY = nativeKey
        APP_OPEN_AD_KEY = openKey
    }
}