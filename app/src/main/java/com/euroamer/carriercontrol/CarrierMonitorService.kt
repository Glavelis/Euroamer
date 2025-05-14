package com.euroamer.carriercontrol

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.telephony.TelephonyCallback
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import kotlinx.coroutines.*

class CarrierMonitorService : Service() {
    private val TAG = "CarrierMonitorService"
    private lateinit var telephonyManager: TelephonyManager
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    
    // List of EU country codes (MCC)
    private val euMobileCodes = setOf(
        "232", // Austria
        "206", // Belgium
        "284", // Bulgaria
        "219", // Croatia
        "280", // Cyprus
        "230", // Czech Republic
        "238", // Denmark
        "248", // Estonia
        "244", // Finland
        "208", // France
        "262", // Germany
        "202", // Greece
        "216", // Hungary
        "272", // Ireland
        "222", // Italy
        "247", // Latvia
        "246", // Lithuania
        "270", // Luxembourg
        "278", // Malta
        "204", // Netherlands
        "260", // Poland
        "268", // Portugal
        "226", // Romania
        "231", // Slovakia
        "293", // Slovenia
        "214", // Spain
        "240"  // Sweden
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val telephonyCallback = object : TelephonyCallback(), TelephonyCallback.CarrierNetworkCallback {
        override fun onCarrierNetworkChange(active: Boolean) {
            handleCarrierChange()
        }
    }

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.registerTelephonyCallback(
                context.getMainExecutor(),
                telephonyCallback
            )
        }
        
        startMonitoring()
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                handleCarrierChange()
                delay(30000) // Check every 30 seconds
            }
        }
    }

    private fun handleCarrierChange() {
        try {
            val networkOperator = telephonyManager.networkOperator
            if (networkOperator.length >= 3) {
                val mcc = networkOperator.substring(0, 3)
                if (!euMobileCodes.contains(mcc)) {
                    // If not an EU carrier, attempt to block it
                    blockNonEuCarrier()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling carrier change", e)
        }
    }

    private fun blockNonEuCarrier() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                // Attempt to force the device to use only EU carriers
                telephonyManager.setPreferredNetworkTypeBitmask(
                    TelephonyManager.NETWORK_TYPE_BITMASK_LTE or
                    TelephonyManager.NETWORK_TYPE_BITMASK_NR
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to block non-EU carrier", e)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.unregisterTelephonyCallback(telephonyCallback)
        }
        job.cancel()
    }
} 