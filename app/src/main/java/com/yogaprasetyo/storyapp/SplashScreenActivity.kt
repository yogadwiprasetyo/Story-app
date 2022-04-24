package com.yogaprasetyo.storyapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.yogaprasetyo.storyapp.ui.WelcomeActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }
}