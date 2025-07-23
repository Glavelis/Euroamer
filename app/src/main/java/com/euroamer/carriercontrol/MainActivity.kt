package com.euroamer.carriercontrol

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.telephony.TelephonyManager
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.graphics.Color
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.TilesOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity(), LocationListener {
    private val PERMISSIONS_REQUEST_CODE = 123
    private lateinit var serviceSwitch: Switch
    private lateinit var statusText: TextView
    private lateinit var currentCarrierText: TextView
    private lateinit var euStatusText: TextView
    private lateinit var checkCarrierButton: Button
    private lateinit var mapView: MapView
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private lateinit var euBoundaryChecker: EUBoundaryChecker
    private lateinit var mapOverlayManager: MapOverlayManager
    private var currentLocationMarker: Marker? = null

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Initialize osmdroid configuration
            Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
            
            setContentView(R.layout.activity_main)

            serviceSwitch = findViewById(R.id.serviceSwitch)
            statusText = findViewById(R.id.statusText)
            currentCarrierText = findViewById(R.id.currentCarrierText)
            euStatusText = findViewById(R.id.euStatusText)
            checkCarrierButton = findViewById(R.id.checkCarrierButton)
            mapView = findViewById(R.id.mapView)
            
            euBoundaryChecker = EUBoundaryChecker()
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            mapOverlayManager = MapOverlayManager(mapView)

            setupUI()
            setupMap()
            checkPermissions()
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting app: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI() {
        serviceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (hasAllPermissions()) {
                    startCarrierService()
                    startLocationUpdates()
                } else {
                    serviceSwitch.isChecked = false
                    checkPermissions()
                }
            } else {
                stopCarrierService()
                stopLocationUpdates()
            }
        }

        checkCarrierButton.setOnClickListener {
            updateCarrierInfo()
        }
    }
    
    private fun setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0) // Approximately 500 meters zoom level
        mapView.controller.setCenter(GeoPoint(50.0, 10.0)) // Center on Europe
        
        // Add location overlay
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)
        
        // Add EU boundary overlays
        mapOverlayManager.addEUBoundaryOverlays()
        
        // Set night mode for map
        mapView.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
    }

    private fun startCarrierService() {
        val serviceIntent = Intent(this, CarrierMonitorService::class.java)
        startService(serviceIntent)
        statusText.text = "Service Status: Running"
        updateCarrierInfo()
    }

    private fun stopCarrierService() {
        val serviceIntent = Intent(this, CarrierMonitorService::class.java)
        stopService(serviceIntent)
        statusText.text = "Service Status: Stopped"
    }

    private fun updateCarrierInfo() {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        try {
            val operatorName = telephonyManager.networkOperatorName
            val networkOperator = telephonyManager.networkOperator
            val mcc = if (networkOperator.length >= 3) networkOperator.substring(0, 3) else "Unknown"
            currentCarrierText.text = "Current Carrier: $operatorName\nMCC: $mcc"
            
            // Check if it's an EU carrier
            val euMobileCodes = setOf(
                "232", "206", "284", "219", "280", "230", "238", "248", "244", "208", "262", "202", "216", "272", "222", "247", "246", "270", "278", "204", "260", "268", "226", "231", "293", "214", "240"
            )
            
            if (euMobileCodes.contains(mcc)) {
                euStatusText.text = "You are connected to an EU Carrier"
                euStatusText.setTextColor(android.graphics.Color.GREEN)
            } else {
                euStatusText.text = "You are not connected to an EU Carrier"
                euStatusText.setTextColor(android.graphics.Color.RED)
            }
        } catch (e: Exception) {
            currentCarrierText.text = "Current Carrier: Unknown"
            euStatusText.text = ""
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSIONS_REQUEST_CODE)
        } else {
            // If we have permissions, start location updates if service is enabled
            if (serviceSwitch.isChecked) {
                startLocationUpdates()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
                if (serviceSwitch.isChecked) {
                    startLocationUpdates()
                }
            } else {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show()
                serviceSwitch.isChecked = false
            }
        }
    }
    
    private fun startLocationUpdates() {
        if (hasAllPermissions()) {
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
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun stopLocationUpdates() {
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            // Ignore
        }
    }
    
    override fun onLocationChanged(location: Location) {
        val geoPoint = GeoPoint(location.latitude, location.longitude)
        
        // Update map center
        mapView.controller.animateTo(geoPoint)
        
        // Remove previous marker
        currentLocationMarker?.let { mapView.overlays.remove(it) }
        
        // Add new location marker
        currentLocationMarker = Marker(mapView)
        currentLocationMarker?.let { marker ->
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "Current Location"
            
            // Check if in EU and update marker color
            val (isInEU, country) = euBoundaryChecker.isLocationInEU(geoPoint)
            if (isInEU) {
                marker.icon = resources.getDrawable(android.R.drawable.ic_menu_mylocation, theme)
                marker.snippet = "In EU: $country"
                euStatusText.text = "Location: In EU ($country)"
                euStatusText.setTextColor(Color.GREEN)
            } else {
                marker.snippet = "Outside EU"
                euStatusText.text = "Location: Outside EU"
                euStatusText.setTextColor(Color.RED)
            }
            
            mapView.overlays.add(marker)
        }
        
        mapView.invalidate()
    }
    
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        mapView.onPause()
        stopLocationUpdates()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }
} 