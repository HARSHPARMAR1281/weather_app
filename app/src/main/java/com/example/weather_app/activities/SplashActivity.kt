package com.example.weather_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weather_app.R
import com.example.weather_app.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(Constants.SPLASH_DELAY)
            navigateToNextScreen()
        }
    }

    private fun navigateToNextScreen() {
        // TODO: Check if user is logged in
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
} 