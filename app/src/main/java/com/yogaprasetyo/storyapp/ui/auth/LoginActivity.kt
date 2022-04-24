package com.yogaprasetyo.storyapp.ui.auth

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
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.databinding.ActivityLoginBinding
import com.yogaprasetyo.storyapp.model.UserLoginModel
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.ui.stories.fragment.MainActivity
import com.yogaprasetyo.storyapp.util.showToast

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.responseLogin.observe(this) { data ->
            // On loading state
            binding.progressbar.isVisible = data.message.isEmpty()

            // On success state
            if (!data.error && data.message.isNotEmpty()) {
                moveToMainActivity()
            }

            // On error state
            if (data.error) {
                showToast(this, data.message)
                return@observe
            }
        }

        binding.btnLogin.setOnClickListener {
            if (binding.cvEmail.text.toString().isEmpty()) {
                binding.cvEmail.error = getString(R.string.input_error_empty)
                return@setOnClickListener
            }

            if (binding.cvPassword.text.toString().isEmpty()) {
                binding.cvPassword.error = getString(R.string.input_error_empty)
                return@setOnClickListener
            }

            if (isInputReady()) {
                sendRequestToServer()
            }
        }

        setupFullScreen()
        playAnimation()
    }

    /**
     * Checking all field value and error
     * If still empty or error, not ready and show info
     * else continue logic
     * */
    private fun isInputReady(): Boolean {
        val isDataInputReady =
            binding.cvEmail.text.toString().isNotEmpty() && binding.cvPassword.text.toString()
                .isNotEmpty()
        val isPassValidation =
            binding.cvEmail.error.isNullOrEmpty() && binding.cvPassword.error.isNullOrEmpty()
        return isDataInputReady && isPassValidation
    }

    /**
     * Move activity to All List Story,
     * then remove stack activity and user cannot back to page login
     * */
    private fun moveToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Send request to the server if input pass all requirement
     * */
    private fun sendRequestToServer() {
        val user = UserLoginModel(
            email = binding.cvEmail.text.toString().trim(),
            password = binding.cvPassword.text.toString()
        )
        val pref = UserPreferences.getInstance(dataStore)
        viewModel.login(user, pref)
    }

    /**
     * Animation property for layout
     * */
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.loginActivity, View.ALPHA, 1f).apply {
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