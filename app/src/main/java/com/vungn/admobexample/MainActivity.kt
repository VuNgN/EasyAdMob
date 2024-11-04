package com.vungn.admobexample

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vungn.admob.manager.GoogleAdsConsentManager
import com.vungn.admob.util.AdModeConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val testDeviceIds = listOf("5051A79634A2CE2BE8EAC9A44E4AD2E7")
        GoogleAdsConsentManager.getInstance(this).gatherConsent(activity = this,
            timeout = 1500,
            debug = true,
            testDeviceIds = testDeviceIds,
            listener = object : GoogleAdsConsentManager.GatherConsentListener {
                override fun onCanShowAds() {
                    AdModeConfig.initAds(testDeviceIds = testDeviceIds)
                }

                override fun onDisableAds() {
                    AdModeConfig.initAds(testDeviceIds = testDeviceIds)
                }
            })

        findViewById<TextView>(R.id.banner).setOnClickListener {
            val intent = Intent(this, BannerExampleActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.native_ad).setOnClickListener {
            val intent = Intent(this, NativeExampleActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.interstitial).setOnClickListener {
            val intent = Intent(this, InterExampleActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.rewarded).setOnClickListener {
            val intent = Intent(this, RewardedExampleActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.open).setOnClickListener {
            val intent = Intent(this, OpenExampleActivity::class.java)
            startActivity(intent)
        }
    }
}