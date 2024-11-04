package com.vungn.admob.manager

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.vungn.admob.util.CMPUtils

class GoogleAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)
    private val cmpUtils: CMPUtils = CMPUtils(context)

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    /** Helper variable to determine if the privacy options form is required. */
    private val isPrivacyOptionsRequired: Boolean get() = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    private fun reset() {
        consentInformation.reset()
    }

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        timeout: Long = 1500,
        debug: Boolean = false,
        testDeviceIds: List<String> = emptyList(),
        listener: GatherConsentListener
    ) {
        if (debug) {
            reset()
        }
        val debugSettings = ConsentDebugSettings.Builder(activity).let {
            it.setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            testDeviceIds.forEach { id ->
                it.addTestDeviceHashedId(id)
            }
            it.build()
        }
        val params =
            if (debug) ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings)
                .build() else ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false)
                .build()
        consentInformation.requestConsentInfoUpdate(activity, params, {
            if (isPrivacyOptionsRequired) {
                if (cmpUtils.requiredShowCMPDialog()) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        cmpUtils.isCheckGDPR = true
                        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
                            if (canRequestAds) {
                                listener.onCanShowAds()
                            } else {
                                listener.onDisableAds()
                            }
                        }
                    }, timeout)
                } else {
                    listener.onCanShowAds()
                }
            } else {
                listener.onCanShowAds()
            }
        }, {
            if (canRequestAds) {
                listener.onCanShowAds()
            } else {
                listener.onDisableAds()
            }
        })
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    companion object {
        @Volatile
        private var instance: GoogleAdsConsentManager? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: GoogleAdsConsentManager(context).also { instance = it }
        }
    }

    interface GatherConsentListener {
        fun onCanShowAds()
        fun onDisableAds()
    }
}