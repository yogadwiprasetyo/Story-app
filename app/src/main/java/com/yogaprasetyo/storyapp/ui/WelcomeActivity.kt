package com.yogaprasetyo.storyapp.ui

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.databinding.ActivityWelcomeBinding
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.ui.auth.LoginActivity
import com.yogaprasetyo.storyapp.ui.auth.RegisterActivity
import com.yogaprasetyo.storyapp.ui.stories.fragment.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var preference: UserPreferences

    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = UserPreferences.getInstance(dataStore)

        /**
         * Load datastore on local storage to checking user is login or not
         * If login, move to stories activity and remove stack activity
         * */
        viewModel.loadPreferences(preference).observe(this) { pref ->
            if (pref.isLogin) moveToMainActivity()
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        setupFullScreen()
        playAnimation()
    }

    /**
     * Move to all list story,
     * then remove stack activity and user cannot go back
     * */
    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Animation property for layout
     * */
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.welcomeActivity, View.ALPHA, 1f).apply {
            duration = 1000
            start()
        }
    }

    @Suppress("DEPRECATION")
    private fun setupFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}