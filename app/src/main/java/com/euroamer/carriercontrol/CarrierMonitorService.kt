package com.euroamer.carriercontrol

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.util.Log
import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.*

class CarrierMonitorService : LifecycleService() {
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

    @Suppress("DEPRECATION")
    private val phoneStateListener = object : PhoneStateListener() {
        override fun onDataConnectionStateChanged(state: Int) {
            super.onDataConnectionStateChanged(state)
            handleCarrierChange()
        }
    }

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        @Suppress("DEPRECATION")
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE)
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
        try {
            // On modern Android versions, we can't directly block carriers
            // Instead, we'll notify the user and suggest manual carrier selection
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting")
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open carrier selection", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        @Suppress("DEPRECATION")
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        job.cancel()
    }
} 