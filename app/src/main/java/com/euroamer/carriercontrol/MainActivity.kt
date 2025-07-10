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

class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 123
    private lateinit var serviceSwitch: Switch
    private lateinit var statusText: TextView
    private lateinit var currentCarrierText: TextView
    private lateinit var euStatusText: TextView
    private lateinit var checkCarrierButton: Button

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)

            serviceSwitch = findViewById(R.id.serviceSwitch)
            statusText = findViewById(R.id.statusText)
            currentCarrierText = findViewById(R.id.currentCarrierText)
            euStatusText = findViewById(R.id.euStatusText)
            checkCarrierButton = findViewById(R.id.checkCarrierButton)

            setupUI()
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
                } else {
                    serviceSwitch.isChecked = false
                    checkPermissions()
                }
            } else {
                stopCarrierService()
            }
        }

        checkCarrierButton.setOnClickListener {
            updateCarrierInfo()
        }
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
            } else {
                Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show()
                serviceSwitch.isChecked = false
            }
        }
    }
} 