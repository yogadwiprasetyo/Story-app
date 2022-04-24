package com.yogaprasetyo.storyapp.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.databinding.ActivityRegisterBinding
import com.yogaprasetyo.storyapp.model.UserRegisterModel
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.util.showToast

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.responseRegister.observe(this) { data ->
            // On loading state
            binding.progressbar.isVisible = data.message.isEmpty()

            // On success state
            if (!data.error && data.message.isNotEmpty()) {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            // On error state
            if (data.error) {
                showToast(this, data.message)
                return@observe
            }
        }

        binding.btnRegister.setOnClickListener {
            // Checking empty field name
            if (binding.etName.text.toString().isEmpty()) {
                binding.etName.error = getString(R.string.input_error_empty)
                return@setOnClickListener
            }

            // Checking empty field email
            if (binding.cvEmail.text.toString().isEmpty()) {
                binding.cvEmail.error = getString(R.string.input_error_empty)
                return@setOnClickListener
            }

            // Checking empty field password
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
            binding.etName.text.toString().trim().isNotEmpty() && binding.cvEmail.text.toString()
                .isNotEmpty() && binding.cvPassword.text.toString().isNotEmpty()
        val isPassValidation =
            binding.cvEmail.error.isNullOrEmpty() && binding.cvPassword.error.isNullOrEmpty()
        return isDataInputReady && isPassValidation
    }

    /**
     * Send request to the server if input pass all requirement
     * */
    private fun sendRequestToServer() {
        val newUser = UserRegisterModel(
            binding.etName.text.toString().trim(),
            binding.cvEmail.text.toString().trim(),
            binding.cvPassword.text.toString().trim()
        )
        viewModel.register(newUser)
    }

    /**
     * Animation property for layout
     * */
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.registerActivity, View.ALPHA, 1f).apply {
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