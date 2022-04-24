package com.yogaprasetyo.storyapp.ui.stories

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.databinding.ActivityNewStoryBinding
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.ui.WelcomeActivity
import com.yogaprasetyo.storyapp.ui.stories.fragment.MainActivity
import com.yogaprasetyo.storyapp.util.reduceFileImage
import com.yogaprasetyo.storyapp.util.rotateBitmap
import com.yogaprasetyo.storyapp.util.showToast
import com.yogaprasetyo.storyapp.util.uriToFile
import java.io.File
import kotlin.random.Random

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class NewStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var preference: UserPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var token: String

    private var getFile: File? = null
    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /**
         * Observe datastore on local storage to retrieve token
         * */
        preference = UserPreferences.getInstance(dataStore)
        viewModel.loadPreferences(preference).observe(this) { pref ->
            token = pref.token
        }

        /**
         * Observe upload story response for checking state or result
         * */
        viewModel.responseStory.observe(this) { data ->
            // On loading state
            binding.progressbar.isVisible = data.message.isEmpty()

            // On success state
            if (!data.error && data.message.isNotEmpty()) {
                startActivity(Intent(this@NewStoryActivity, MainActivity::class.java))
                finish()
            }

            // On error state
            if (data.error) {
                showToast(this, data.message)
                return@observe
            }
        }

        binding.btnUpload.setOnClickListener {
            if (!isFileNotNull()) {
                showToast(this@NewStoryActivity, getString(R.string.input_error_image))
                return@setOnClickListener
            }

            if (!isDescNotEmpty()) {
                binding.etDescription.error = getString(R.string.input_error_empty)
                return@setOnClickListener
            }

            /**
             * If input for request is ready
             * send request to API
             * */
            if (isRequestBodyReady()) {
                sendRequestToServer()
            }
        }

        binding.ibCamera.setOnClickListener {
            // Ask for the permissions to user
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
                return@setOnClickListener
            }

            // launch camera if permission is granted
            startCameraX()
        }

        binding.ibGallery.setOnClickListener { startGallery() }

        binding.ibLocation.setOnClickListener { getLocationNow() }
    }

    /**
     * Send request if all input is ready
     * */
    private fun sendRequestToServer() {
        val address = binding.etLocation.text.toString()
        val location = addressToCoordinate(address)
        val file = reduceFileImage(getFile as File)
        val description = binding.etDescription.text.toString()

        viewModel.uploadStory(token, file, description, location)
    }

    /**
     * Checking all field value
     * If still empty show info error
     * else continue logic
     * */
    private fun isRequestBodyReady(): Boolean {
        return isFileNotNull() && isDescNotEmpty()
    }

    private fun isFileNotNull() = getFile != null

    private fun isDescNotEmpty() = binding.etDescription.text.toString().trim().isNotEmpty()

    /**
     * Launch cameraX
     * */
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    /**
     * Open folder with only show file type is image
     * */
    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    /**
     * Handle all result permissions from user
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showToast(this, getString(R.string.camera_permission_denied))
            }
        }
    }

    /**
     * Check all permission is allowed or not
     * */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Launcher to Camera Activity and receive result after finished
     * */
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile: File = it.data?.getSerializableExtra(EXTRA_PHOTO) as File
            val isBackCamera = it.data?.getBooleanExtra(ISBACKCAMERA, true) as Boolean
            getFile = myFile

            val result = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                BitmapFactory.decodeFile(myFile.path)
            } else {
                rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)
            }

            binding.ivPreview.setImageBitmap(result)
        }
    }

    /**
     * Launcher to gallery on client
     * */
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImage, this@NewStoryActivity)

            getFile = myFile
            binding.ivPreview.setImageURI(selectedImage)
        }
    }

    /**
     * Launcher to turn on GPS for the location
     * */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> getLocationNow()
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> getLocationNow()
            else -> {}
        }
    }

    /**
     * Check all permission for location
     * */
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Handle if success get location user
     * */
    private fun getLocationNow() {
        if (
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val addressName = getAddressName(LatLng(location.latitude, location.longitude))
                    binding.etLocation.setText(addressName)
                } else {
                    showToast(this, getString(R.string.empty_location))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    /**
     * Get address name from latitude and longitude,
     * if not detected, jus return "Unknown address"
     * */
    private fun getAddressName(latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(this)
            val allAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (allAddress.isEmpty()) getString(R.string.empty_address) else allAddress[0].getAddressLine(
                0
            )
        } catch (e: Exception) {
            getString(R.string.empty_address)
        }
    }

    /**
     * Convert location name to coordinate latitude and longitude
     * and if result not found, return random coordinate
     * and if error, return zero coordinate
     * */
    private fun addressToCoordinate(locationName: String): LatLng {
        return try {
            val randomLatitude = randomCoordinate()
            val randomLongitude = randomCoordinate()

            val geocoder = Geocoder(this)
            val allLocation = geocoder.getFromLocationName(locationName, 1)
            if (allLocation.isEmpty()) {
                LatLng(randomLatitude, randomLongitude)
            } else {
                LatLng(allLocation[0].latitude, allLocation[0].longitude)
            }
        } catch (e: Exception) {
            LatLng(0.0, 0.0)
        }
    }

    /**
     * Generate random double number for coordinate latitude & longitude
     * */
    private fun randomCoordinate(): Double {
        return Random.nextDouble(15.0, 100.0)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /**
             * Remove token and state login on datastore
             * then move to activity welcome and clear stack activity
             * */
            R.id.logout -> {
                viewModel.logout(preference)
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }

            /**
             * Launch intent to global setting language
             * */
            R.id.changeLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val EXTRA_PHOTO = "extra_photo"
        const val ISBACKCAMERA = "extra_isBackCamera"

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}