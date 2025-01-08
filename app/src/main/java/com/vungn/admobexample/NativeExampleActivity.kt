package com.vungn.admobexample

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vungn.admob.manager.NativeAdManager
import com.vungn.admob.util.NativeAd
import com.vungn.admob.util.VideoController
import com.vungn.admob.view.MediaView
import com.vungn.admob.view.NativeAdView

class NativeExampleActivity : AppCompatActivity() {
    private lateinit var videoStatus: TextView
    private lateinit var loadAd: Button
    private lateinit var startVideoAdsMuted: CheckBox
    private lateinit var nativeAdManager: NativeAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_native_example)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        videoStatus = findViewById(R.id.tv_video_status)
        loadAd = findViewById(R.id.btn_load)
        startVideoAdsMuted = findViewById(R.id.cb_start_muted)

        nativeAdManager = NativeAdManager(this)

        loadAd.setOnClickListener {
            loadAd.setEnabled(false)
            nativeAdManager.loadAd(
                videoMuted = startVideoAdsMuted.isChecked,
                listener = object : NativeAdManager.NativeAdLoadListener() {
                    override fun onAdLoaded(currentNativeAd: NativeAd) {
                        val frameLayout = findViewById<FrameLayout>(R.id.fl_adplaceholder)
                        val adView: NativeAdView =
                            layoutInflater.inflate(
                                R.layout.ad_unified,
                                frameLayout,
                                false
                            ) as NativeAdView
                        populateNativeAdView(currentNativeAd, adView)
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                        loadAd.setEnabled(true);
                    }

                    override fun onAdFailedToLoad() {
                        loadAd.setEnabled(true)
                    }
                })
        }
    }


    /**
     * Populates a [NativeAdView] object with data from a given [NativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView   the view to be populated
     */
    private fun populateNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        // Set the media view.
        adView.setMediaView(adView.findViewById<View>(R.id.ad_media) as MediaView)

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById<View>(R.id.ad_headline))
        adView.setBodyView(adView.findViewById<View>(R.id.ad_body))
        adView.setCallToActionView(adView.findViewById<View>(R.id.ad_call_to_action))
        adView.setIconView(adView.findViewById<View>(R.id.ad_app_icon))
        adView.setPriceView(adView.findViewById<View>(R.id.ad_price))
        adView.setStarRatingView(adView.findViewById<View>(R.id.ad_stars))
        adView.setStoreView(adView.findViewById<View>(R.id.ad_store))
        adView.setAdvertiserView(adView.findViewById<View>(R.id.ad_advertiser))

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.getHeadlineView() as TextView).text = nativeAd.getHeadline()
        adView.getMediaView()?.setMediaContent(nativeAd.getMediaContent())

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView()?.visibility = View.INVISIBLE
        } else {
            adView.getBodyView()?.visibility = View.VISIBLE
            (adView.getBodyView() as TextView).text = nativeAd.getBody()
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView()?.visibility = View.INVISIBLE
        } else {
            adView.getCallToActionView()?.visibility = View.VISIBLE
            (adView.getCallToActionView() as Button).text = nativeAd.getCallToAction()
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView()?.visibility = View.GONE
        } else {
            (adView.getIconView() as ImageView).setImageDrawable(
                nativeAd.getIcon()?.getDrawable()
            )
            adView.getIconView()?.visibility = View.VISIBLE
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView()?.visibility = View.INVISIBLE
        } else {
            adView.getPriceView()?.visibility = View.VISIBLE
            (adView.getPriceView() as TextView).text = nativeAd.getPrice()
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView()?.visibility = View.INVISIBLE
        } else {
            adView.getStoreView()?.visibility = View.VISIBLE
            (adView.getStoreView() as TextView).text = nativeAd.getStore()
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView()?.visibility = View.INVISIBLE
        } else {
            (adView.getStarRatingView() as RatingBar).rating =
                nativeAd.getStarRating()?.toFloat() ?: 0f
            adView.getStarRatingView()?.visibility = View.VISIBLE
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView()?.visibility = View.INVISIBLE
        } else {
            (adView.getAdvertiserView() as TextView).text = nativeAd.getAdvertiser()
            adView.getAdvertiserView()?.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc: VideoController? = nativeAd.getMediaContent()?.getVideoController()

        // Updates the UI to say whether or not this ad has a video asset.
        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent()
                ?.hasVideoContent() == true
        ) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.

            vc?.setVideoLifecycleCallbacks(object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    loadAd.setEnabled(true)
                    videoStatus.text = "Video status: Video playback has ended."
                    super.onVideoEnd()
                }
            })
        } else {
            loadAd.setEnabled(true)
            videoStatus.text = "Video status: Ad does not contain a video asset."
        }
    }
}