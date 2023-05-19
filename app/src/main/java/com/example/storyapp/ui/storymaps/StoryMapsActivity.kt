package com.example.storyapp.ui.storymaps

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.data.repository.AuthRepository
import com.example.storyapp.data.viewmodelfactory.StoryViewModelFactory
import com.example.storyapp.databinding.ActivityStoryMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding

    private lateinit var token: String

    private val storyMapsViewModel by viewModels<StoryMapsViewModel>{
        StoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(AuthRepository.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        token = preferences.getString(AuthRepository.TOKEN_KEY, null)!!

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        //Custom Retro Style Map
        setMapStyle()

        storyMapsViewModel.getStoryLocation("Bearer $token")

        addMarkers()
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addMarkers() {
        storyMapsViewModel.storyLocation.observe(this) {
            it.forEach { coordinateList ->
                val latLng = LatLng(coordinateList.lat.toString().toDouble(),
                    coordinateList.lon.toString().toDouble())
                Log.d(TAG, "COORDINATES : $latLng")

                //For some reason ini tidak muncul?
                //Perlu masukan
                mMap.addMarker(MarkerOptions()
                    .position(latLng)
                    .title(coordinateList.name)
                    .snippet(coordinateList.description)
                )
                boundsBuilder.include(latLng)
            }

            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    companion object{
        private const val TAG = "StoryMapsActivity"
    }
}