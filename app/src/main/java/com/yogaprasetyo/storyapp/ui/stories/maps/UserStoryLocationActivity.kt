package com.yogaprasetyo.storyapp.ui.stories.maps

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.yogaprasetyo.storyapp.R
import com.yogaprasetyo.storyapp.data.local.UserPreferences
import com.yogaprasetyo.storyapp.data.remote.response.ListStoryItem
import com.yogaprasetyo.storyapp.databinding.ActivityUserStoryLocationBinding
import com.yogaprasetyo.storyapp.model.UserViewModel
import com.yogaprasetyo.storyapp.model.ViewModelFactory
import com.yogaprasetyo.storyapp.ui.stories.fragment.MainActivity
import com.yogaprasetyo.storyapp.util.showToast

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserStoryLocationActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityUserStoryLocationBinding
    private lateinit var storiesWithLocation: List<ListStoryItem>

    private val boundsBuilder = LatLngBounds.Builder()
    private val viewModel: UserViewModel by viewModels { ViewModelFactory.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserStoryLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        viewModel.loadPreferences(UserPreferences.getInstance(dataStore)).observe(this) { pref ->
            viewModel.loadAllStoriesWithLocation(pref.token).observe(this) { response ->
                // On success or error states
                val isSuccessOrError = !response.error && response.message.isNotEmpty()
                if (isSuccessOrError) {
                    storiesWithLocation = response.listStory
                    mapFragment.getMapAsync(this)
                }
            }
        }

        binding.efbListStory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        setupFullScreen()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapUI()
        setMapStyle()
        setupLocationFromStories()
        mMap.setOnMarkerClickListener(this)
    }

    /**
     * Zoom to the position with city scale and
     * show the name of address
     * */
    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker.isInfoWindowShown) {
            marker.hideInfoWindow()
        } else {
            marker.showInfoWindow()
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 10f))
        return true
    }

    /**
     * Add marker to the position with title
     * then show address name on last position
     *
     * If no location is receive, just end it
     * */
    private fun setupLocationFromStories() {
        if (storiesWithLocation.isNullOrEmpty()) return

        storiesWithLocation.forEach { story ->
            val location = LatLng(story.lat, story.lon)
            val address = getAddressName(location)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(address)
            )

            boundsBuilder.include(location)
            marker?.showInfoWindow()
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))
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
     * Setup custom map style,
     * if not found custom style, just use default style
     * */
    private fun setMapStyle() {
        try {
            val mapStyle = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            val success = mMap.setMapStyle(mapStyle)
            if (!success) {
                showToast(this, getString(R.string.empty_map_style))
            }
        } catch (exception: Resources.NotFoundException) {
            showToast(this, getString(R.string.empty_map_style))
        }
    }

    private fun setMapUI() {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
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