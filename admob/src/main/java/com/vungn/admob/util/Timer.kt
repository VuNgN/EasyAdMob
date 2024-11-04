package com.vungn.admob.util

import android.os.CountDownTimer
import com.vungn.admob.util.AdModeConfig.COUNTER_TIME_MILLISECONDS
import java.util.concurrent.TimeUnit

class Timer {
    private var _timer: CountDownTimer? = null
    private var _secondsRemaining: Long = 0
    fun finish() {
        _timer?.onFinish()
        _timer?.cancel()
        _timer = null
    }

    fun start(timeout: Long = COUNTER_TIME_MILLISECONDS, onFinish: () -> Unit) {
        _timer = object : CountDownTimer(timeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
            }

            override fun onFinish() {
                _secondsRemaining = 0
                onFinish()
            }
        }
        _timer?.start()
    }
}