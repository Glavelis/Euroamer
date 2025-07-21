package com.euroamer.carriercontrol

import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.content.Context

class LocationService : Service(), LocationListener {
    
    private lateinit var locationManager: LocationManager
    private val binder = LocationBinder()
    private var locationCallback: ((Location) -> Unit)? = null
    
    inner class LocationBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    
    fun setLocationCallback(callback: (Location) -> Unit) {
        locationCallback = callback
    }
    
    fun startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L, // 5 seconds
                10f,   // 10 meters
                this
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000L,
                10f,
                this
            )
        } catch (e: SecurityException) {
            // Handle permission error
        }
    }
    
    fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            // Ignore
        }
    }
    
    override fun onLocationChanged(location: Location) {
        locationCallback?.invoke(location)
    }
    
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    
    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }
}