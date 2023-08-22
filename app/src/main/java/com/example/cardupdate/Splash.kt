package com.example.cardupdate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.orange)
        Handler().postDelayed(Runnable { /*
        Create an Intent that will start the Menu-Activity. */
            val prefs = getSharedPreferences("MyPref", MODE_PRIVATE)
            if(prefs!=null) {
                val time = prefs.getLong("time", 86400000)
                if (time+86400000 - System.currentTimeMillis() > 0) {
                    val mainIntent = Intent(this@Splash, TimerScreen::class.java)
                    this@Splash.startActivity(mainIntent)
                    finish()
                } else {
                    val mainIntent = Intent(this@Splash, PermissionActiivty::class.java)
                    this@Splash.startActivity(mainIntent)
                    finish()
                }
            }
            else{
                val mainIntent = Intent(this@Splash, PermissionActiivty::class.java)
                this@Splash.startActivity(mainIntent)
                finish()

            }
        }, 3000)
    }

}