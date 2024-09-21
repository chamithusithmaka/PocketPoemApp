package com.example.act3

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class TimerService : Service() {

    companion object {
        const val ACTION_START = "com.example.act3.action.START"
        const val ACTION_STOP = "com.example.act3.action.STOP"
        const val ACTION_UPDATE = "com.example.act3.action.UPDATE"
        const val EXTRA_TIME = "com.example.act3.extra.TIME"
        const val EXTRA_REMAINING_TIME = "com.example.act3.extra.REMAINING_TIME"
    }

    private var countDownTimer: CountDownTimer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val timeInMillis = intent.getLongExtra(EXTRA_TIME, 0L)
                startTimer(timeInMillis)
            }
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(timeInMillis: Long) {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val updateIntent = Intent(ACTION_UPDATE)
                updateIntent.putExtra(EXTRA_REMAINING_TIME, millisUntilFinished)
                sendBroadcast(updateIntent)
            }

            override fun onFinish() {
                stopSelf()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
