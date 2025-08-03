package com.euroamer.carriercontrol

import android.content.Context
import android.location.Location
import android.os.Environment
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class TrackingManager(private val context: Context) {
    private val trackPoints = mutableListOf<TrackPoint>()
    private var isTracking = false
    private var trackStartTime: Date? = null
    private var startingPlace: String = "Unknown"
    
    data class TrackPoint(
        val latitude: Double,
        val longitude: Double,
        val altitude: Double,
        val speed: Float,
        val timestamp: Date
    )
    
    fun startTracking(location: Location) {
        isTracking = true
        trackStartTime = Date()
        trackPoints.clear()
        startingPlace = getLocationName(location.latitude, location.longitude)
        addTrackPoint(location)
    }
    
    fun stopTracking(): String? {
        if (!isTracking) return null
        isTracking = false
        return exportToGPX()
    }
    
    fun addTrackPoint(location: Location) {
        if (!isTracking) return
        
        val trackPoint = TrackPoint(
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = if (location.hasAltitude()) location.altitude else 0.0,
            speed = if (location.hasSpeed()) location.speed else 0f,
            timestamp = Date()
        )
        trackPoints.add(trackPoint)
    }
    
    private fun getLocationName(lat: Double, lon: Double): String {
        // Simplified location naming - in production, use geocoding
        return "Track_${lat.toInt()}_${lon.toInt()}"
    }
    
    private fun exportToGPX(): String? {
        if (trackPoints.isEmpty()) return null
        
        val trackingDir = File(context.getExternalFilesDir(null), "Tracking")
        if (!trackingDir.exists()) {
            trackingDir.mkdirs()
        }
        
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val timeString = trackStartTime?.let { dateFormat.format(it) } ?: dateFormat.format(Date())
        val filename = "${startingPlace}_${timeString}.gpx"
        val gpxFile = File(trackingDir, filename)
        
        try {
            FileWriter(gpxFile).use { writer ->
                writer.write(generateGPXContent())
            }
            return gpxFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun generateGPXContent(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        
        val gpx = StringBuilder()
        gpx.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        gpx.append("<gpx version=\"1.1\" creator=\"Euroamer\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n")
        gpx.append("  <trk>\n")
        gpx.append("    <name>$startingPlace</name>\n")
        gpx.append("    <trkseg>\n")
        
        for (point in trackPoints) {
            gpx.append("      <trkpt lat=\"${point.latitude}\" lon=\"${point.longitude}\">\n")
            if (point.altitude != 0.0) {
                gpx.append("        <ele>${point.altitude}</ele>\n")
            }
            gpx.append("        <time>${dateFormat.format(point.timestamp)}</time>\n")
            if (point.speed > 0) {
                gpx.append("        <extensions>\n")
                gpx.append("          <speed>${point.speed}</speed>\n")
                gpx.append("        </extensions>\n")
            }
            gpx.append("      </trkpt>\n")
        }
        
        gpx.append("    </trkseg>\n")
        gpx.append("  </trk>\n")
        gpx.append("</gpx>\n")
        
        return gpx.toString()
    }
    
    fun getTrackingDirectory(): File {
        return File(context.getExternalFilesDir(null), "Tracking")
    }
    
    fun isCurrentlyTracking(): Boolean = isTracking
}