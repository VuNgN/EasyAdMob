package com.vungn.admobexample

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vungn.admob.manager.RewardedAdManager
import com.vungn.admob.util.RewardItem

class RewardedExampleActivity : AppCompatActivity() {
    private lateinit var adManager: RewardedAdManager
    private var countDownTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var coinCount = 0
    private var gamePaused = false
    private var gameOver = false
    private lateinit var coinCountText: TextView
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rewarded_example)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        coinCountText = findViewById(R.id.coin_count_text)
        retryButton = findViewById(R.id.retry_button)
        adManager = RewardedAdManager.Builder(this).setTimeout(3000)
            .addListener(object : RewardedAdManager.AppRewardedAdListener {
                override fun onStateChange(state: RewardedAdManager.State) {
                    when (state) {
                        RewardedAdManager.State.CLOSED -> adManager.loadAd()
                        else -> {}
                    }
                }

                override fun onAdClicked() {}

                override fun onUserEarnedReward(rewardItem: RewardItem) {
                    addCoins(rewardItem.getAmount())
                }
            }).build()
        adManager.loadAd()
        startGame()
        retryButton.setOnClickListener {
            startGame()
            adManager.loadAd()
        }
        // Display current coin count to user.
        coinCountText = findViewById(R.id.coin_count_text)
        coinCount = 0
        coinCountText.text = "Coins: $coinCount"
    }

    override fun onPause() {
        super.onPause()
        pauseGame()
    }

    override fun onResume() {
        super.onResume()
        resumeGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        adManager.clearAllListener()
    }

    private fun pauseGame() {
        if (gameOver || gamePaused) {
            return
        }
        countDownTimer?.cancel()
        gamePaused = true
    }

    private fun resumeGame() {
        if (gameOver || !gamePaused) {
            return
        }
        createTimer(timeRemaining)
        gamePaused = false
    }

    private fun addCoins(coins: Int) {
        coinCount += coins
        coinCountText.text = "Coins: $coinCount"
    }

    private fun startGame() {
        // Hide the retry button, load the ad, and start the timer.
        retryButton.visibility = View.INVISIBLE
        createTimer(COUNTER_TIME)
        gamePaused = false
        gameOver = false
    }

    private fun createTimer(time: Long) {
        val textView: TextView = findViewById(R.id.timer)
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(time * 1000, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = (millisUntilFinished / 1000 + 1)
                textView.text = "seconds remaining: $timeRemaining"
            }

            override fun onFinish() {
                textView.text = "You Lose!"
                addCoins(GAME_OVER_REWARD)
                retryButton.visibility = View.VISIBLE
                gameOver = true

                val rewardItem: RewardItem? = adManager.rewardItem
                val rewardAmount = rewardItem?.getAmount()
                val rewardType = rewardItem?.getType()

                Log.d(TAG, "The rewarded interstitial ad is ready.")
                introduceVideoAd(rewardAmount, rewardType)
            }
        }
        countDownTimer?.start()
    }

    private fun introduceVideoAd(rewardAmount: Int?, rewardType: String?) {
        val dialog = AdDialogFragment.newInstance(rewardAmount, rewardType)
        dialog.setAdDialogInteractionListener(object :
            AdDialogFragment.AdDialogInteractionListener {
            override fun onShowAd() {
                Log.d(TAG, "The rewarded interstitial ad is starting.")
                adManager.showAd()
            }

            override fun onCancelAd() {
                Log.d(TAG, "The rewarded interstitial ad was skipped before it starts.")
            }
        })
        dialog.show(supportFragmentManager, "AdDialogFragment")
    }

    companion object {
        const val COUNTER_TIME = 10L
        const val GAME_OVER_REWARD = 1
        private const val TAG = "RewardedExampleActivity"
    }
}