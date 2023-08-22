package com.example.cardupdate

import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.cardupdate.databinding.ActivityTimerScreenBinding

class TimerScreen : AppCompatActivity() {
    var countDownTimer: CountDownTimer? = null
    lateinit var binding: ActivityTimerScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        initView()
    }

    private fun initView() {
        val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
        val time = prefs.getLong("time", 86400000)
        showTimer(time);
    }

    private fun showTimer(timeUploaded: Long) {
        val time = System.currentTimeMillis()
        val toSet: Long =
            timeUploaded + 86400000 - time
        if (toSet > 0) {
            countDownTimer = object : CountDownTimer(toSet, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = (millisUntilFinished / 1000).toInt() % 60
                    val minutes = (millisUntilFinished / (1000 * 60) % 60).toInt()
                    val hours = (millisUntilFinished / (1000 * 60 * 60) % 24).toInt()
                    binding.timer.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                }


                override fun onFinish() {
                    finish()
                }
            }.start()
        }
    }
}