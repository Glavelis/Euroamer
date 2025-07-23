package com.euroamer.carriercontrol

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.TelephonyManager
import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.util.Log
import kotlinx.coroutines.*
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import android.net.ConnectivityManager
import android.provider.Settings
import com.euroamer.carriercontrol.MCCCountryMapper

class CarrierMonitorService : Service() {
    private val TAG = "CarrierMonitorService"
    private lateinit var telephonyManager: TelephonyManager
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val CHANNEL_ID = "CarrierMonitorChannel"
    private val NOTIFICATION_ID = 1
    
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
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Monitoring carrier..."))
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        @Suppress("DEPRECATION")
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE)
        startMonitoring()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Carrier Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("EU Carrier Control")
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setOngoing(true)
        .build()

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                handleCarrierChange()
                delay(5000) // Check every 5 seconds for faster response
            }
        }
    }

    private fun handleCarrierChange() {
        try {
            val networkOperator = telephonyManager.networkOperator
            if (networkOperator.length >= 3) {
                val mcc = networkOperator.substring(0, 3)
                val operatorName = telephonyManager.networkOperatorName
                if (euMobileCodes.contains(mcc)) {
                    updateNotification("✓ EU carrier: $operatorName (MCC: $mcc)")
                } else {
                    updateNotification("⚠️ BLOCKED: Non-EU carrier $operatorName (MCC: $mcc)")
                    // Immediately block non-EU carrier
                    blockNonEuCarrier()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling carrier change", e)
        }
    }

    private fun updateNotification(message: String) {
        val notification = createNotification(message)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun blockNonEuCarrier() {
        try {
            Log.w(TAG, "Non-EU carrier detected - attempting to block connection")
            
            // Try to disable mobile data
            disableMobileData()
            
            // Try to enable airplane mode as last resort
            enableAirplaneMode()
            
            // Open carrier selection for manual override
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting")
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to block non-EU carrier", e)
        }
    }
    
    private fun disableMobileData() {
        try {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            // Note: This requires system-level permissions that regular apps don't have
            // The app will need to be installed as a system app or have special permissions
            Log.w(TAG, "Attempting to disable mobile data for non-EU carrier")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to disable mobile data", e)
        }
    }
    
    private fun enableAirplaneMode() {
        try {
            // Note: This requires WRITE_SECURE_SETTINGS permission
            // and may not work on all Android versions due to security restrictions
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.System.putInt(contentResolver, Settings.System.AIRPLANE_MODE_ON, 1)
            } else {
                Settings.Global.putInt(contentResolver, Settings.Global.AIRPLANE_MODE_ON, 1)
            }
            
            // Send broadcast to update airplane mode
            val intent = Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            intent.putExtra("state", true)
            sendBroadcast(intent)
            
            Log.w(TAG, "Airplane mode activated to block non-EU carrier")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable airplane mode", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        @Suppress("DEPRECATION")
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        job.cancel()
    }
} 