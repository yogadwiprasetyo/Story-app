package com.yogaprasetyo.storyapp.ui.stories

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.databinding.ActivityCameraBinding
import com.yogaprasetyo.storyapp.util.createFile
import com.yogaprasetyo.storyapp.util.showToast
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoFile: File

    private var imageCapture: ImageCapture? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.captureImage.setOnClickListener { takePhoto() }
        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector.equals(CameraSelector.DEFAULT_BACK_CAMERA)) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        setupFullScreen()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        // binding lifecycle camera to the lifecycle owner
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        /**
         * Setup the camera for preview, image capture, and selector camera (front or back)
         * */
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            // Assign for take photo
            imageCapture = ImageCapture.Builder().build()

            /**
             * Handle error when connecting all setup camera
             * */
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                showToast(this@CameraActivity, getString(R.string.camera_open_failed))
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * Handle for capturing image from camera
     * */
    private fun takePhoto() {

        // Make sure image capture is ready to avoid bug or crash
        val imageCapture = imageCapture ?: return

        // Prepare file for result photo
        // createFile function from Helper.kt
        photoFile = createFile(application)

        // Setup prepare output photo
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Action to take photo from camera
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            mImageCapture
        )

    }

    /**
     * Anonymous object to handle take photo from camera
     * */
    private val mImageCapture = object : ImageCapture.OnImageSavedCallback {

        // Handle when the take photo is successful
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val intent = Intent()
            intent.putExtra(NewStoryActivity.EXTRA_PHOTO, photoFile)
            intent.putExtra(
                NewStoryActivity.ISBACKCAMERA,
                cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
            )
            setResult(NewStoryActivity.CAMERA_X_RESULT, intent)
            finish()
        }

        // Handle when the take photo is failed
        override fun onError(exception: ImageCaptureException) {
            showToast(this@CameraActivity, getString(R.string.failed_take_photo))
        }
    }

    private fun setupFullScreen() {
        @Suppress("DEPRECATION")
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