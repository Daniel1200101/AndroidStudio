package com.example.targil1.Utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class LocationManagerHelper private constructor(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    companion object {
        @Volatile
        private var instance: LocationManagerHelper? = null

        fun init(context: Context): LocationManagerHelper {
            return instance ?: synchronized(this) {
                instance ?: LocationManagerHelper(context).also { instance = it }
            }
        }

        fun getInstance(): LocationManagerHelper {
            return instance ?: throw IllegalStateException(
                "LocationManagerHelper must be initialized"
            )
        }
    }

    fun getCurrentLocationForMap(): Pair<Double, Double> {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Missing location permission")
        }

        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        val bestLocation = when {
            gpsLocation != null && networkLocation != null ->
                if (gpsLocation.time > networkLocation.time) gpsLocation else networkLocation
            gpsLocation != null -> gpsLocation
            networkLocation != null -> networkLocation
            else -> null
        }

        bestLocation?.let {
            return Pair(it.latitude, it.longitude)
        }

        throw Exception("Could not get location. Please ensure GPS is enabled.")
    }
}