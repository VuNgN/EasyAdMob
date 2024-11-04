package com.vungn.admobexample

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vungn.admob.manager.AppBannerAdManager

class BannerExampleActivity : AppCompatActivity() {
    private var adManager: AppBannerAdManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_banner_example)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adViewContainer = findViewById<FrameLayout>(R.id.bannerAd)
        adManager = AppBannerAdManager(this)
        adManager?.loadAd(
            adViewContainer = adViewContainer,
            lifecycle = lifecycle,
            isCollapse = true,
            listener = object : AppBannerAdManager.BannerAdLoadListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }
            }
        )
    }
}