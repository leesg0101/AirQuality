package com.sg0101.app.airquality

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sg0101.app.airquality.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding

    private var googleMap: GoogleMap? = null
    var currentLat: Double = 0.0
    var currentLng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLat = intent.getDoubleExtra("currentLat", 0.0)
        currentLng = intent.getDoubleExtra("currentLng", 0.0)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.btnCheckHere.setOnClickListener {
            googleMap?.let { googleMap ->
                val intent = Intent()
                intent.putExtra("latitude", googleMap.cameraPosition.target.latitude)
                intent.putExtra("longitude", googleMap.cameraPosition.target.longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    // 지도가 준비되었을 때 실행되는 콜백
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        this.googleMap?.let {
            val currentLocation = LatLng(currentLat, currentLng)
            it.apply {
                setMaxZoomPreference(20.0f)
                setMinZoomPreference(12.0f)
                moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
            }
        }
        setMarker()

        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(this)
            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()
            this.googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        latitude,
                        longitude
                    ), 16f
                )
            )
            setMarker()
        }
    }

    private fun setMarker() {
        googleMap?.let { googleMap ->
            googleMap.clear()
            val markerOptions = MarkerOptions()
            markerOptions.position(googleMap.cameraPosition.target)
            markerOptions.title("마커 위치")
            val marker = googleMap.addMarker(markerOptions)
            googleMap.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.position = googleMap.cameraPosition.target
                }
            }
        }
    }
}