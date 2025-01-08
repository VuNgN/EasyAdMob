package com.vungn.admobexample

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vungn.admob.manager.InterstitialAdManager

class InterExampleActivity : AppCompatActivity() {
    private lateinit var adManager: InterstitialAdManager
    private var countDownTimer: CountDownTimer? = null
    private var timerMilliseconds = 0L
    private var gameOver = false
    private var gamePaused = false
    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inter_example)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        retryButton = findViewById(R.id.retry_button)
        adManager = InterstitialAdManager.Builder(this).setTimeout(3000).addListener(
            object : InterstitialAdManager.InterstitialAdListener {
                override fun onStateChange(state: InterstitialAdManager.State) {
                    when (state) {
                        InterstitialAdManager.State.LOADING -> retryButton.isEnabled = false
                        InterstitialAdManager.State.NOT_LOADED -> retryButton.isEnabled = true
                        InterstitialAdManager.State.LOADED -> retryButton.isEnabled = true
                        InterstitialAdManager.State.CLOSED -> {
                            adManager.loadAd()
                            startGame()
                        }

                        else -> {}
                    }
                }
            }
        ).build()
        adManager.loadAd()
        retryButton.visibility = View.INVISIBLE;
        retryButton.setOnClickListener {
            adManager.showAd()
        }
        startGame()
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

    private fun createTimer(milliseconds: Long) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        countDownTimer?.cancel()

        val textView: TextView = findViewById(R.id.timer)

        countDownTimer = object : CountDownTimer(milliseconds, 50) {
            override fun onTick(millisUnitFinished: Long) {
                timerMilliseconds = millisUnitFinished
                textView.text = "seconds remaining: ${millisUnitFinished / 1000 + 1}"
            }

            override fun onFinish() {
                gameOver = true
                textView.text = "done!"
                retryButton.visibility = View.VISIBLE
            }
        }

        countDownTimer?.start()
    }

    private fun startGame() {
        // Hide the button, and kick off the timer.
        retryButton.visibility = View.INVISIBLE
        createTimer(GAME_LENGTH_MILLISECONDS)
        gamePaused = false
        gameOver = false
    }

    private fun resumeGame() {
        if (gameOver || !gamePaused) {
            return
        }
        // Create a new timer for the correct length.
        gamePaused = false
        createTimer(timerMilliseconds)
    }

    private fun pauseGame() {
        if (gameOver || gamePaused) {
            return
        }
        countDownTimer?.cancel()
        gamePaused = true
    }

    companion object {
        const val GAME_LENGTH_MILLISECONDS = 3000L
    }
}