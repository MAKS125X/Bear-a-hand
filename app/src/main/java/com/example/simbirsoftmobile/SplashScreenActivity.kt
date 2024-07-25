package com.example.simbirsoftmobile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = 1000L
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            },
            splashScreenDuration,
        )
    }
}
