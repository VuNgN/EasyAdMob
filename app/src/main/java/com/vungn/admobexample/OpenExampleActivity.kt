package com.vungn.admobexample

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vungn.admob.manager.OpenAdManager

class OpenExampleActivity : AppCompatActivity() {
    private lateinit var adManager: OpenAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_open_ad)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textView = findViewById<TextView>(R.id.main_activity_text)

        adManager = OpenAdManager.Builder(this).setTimeout(3000).addListener(
            object : OpenAdManager.OpenAdListener {
                override fun onStateChange(
                    manager: OpenAdManager,
                    state: OpenAdManager.State
                ) {
                    when (state) {
                        OpenAdManager.State.LOADING -> textView.text = "State Ad: Loading"
                        OpenAdManager.State.NOT_LOADED -> textView.text = "State Ad: Not Loaded"
                        OpenAdManager.State.LOADED -> {
                            textView.text = "State Ad: Loaded"
                            adManager.showAd()
                        }

                        OpenAdManager.State.SHOWING -> textView.text = "State Ad: Showing"
                        OpenAdManager.State.CLOSED -> textView.text =
                            getString(R.string.main_activity_text)

                        else -> {}
                    }
                }
            }
        ).build()
        adManager.loadAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        adManager.clearAllListener()
    }
}