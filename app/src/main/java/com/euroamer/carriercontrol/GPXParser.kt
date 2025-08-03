package com.euroamer.carriercontrol

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class GPXParser {
    
    data class GPXTrack(
        val name: String,
        val points: List<GeoPoint>,
        val speeds: List<Float>
    )
    
    fun parseGPXFile(file: File): GPXTrack? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document: Document = builder.parse(file)
            
            val trackName = getTrackName(document)
            val trackPoints = mutableListOf<GeoPoint>()
            val speeds = mutableListOf<Float>()
            
            val trkptNodes: NodeList = document.getElementsByTagName("trkpt")
            
            for (i in 0 until trkptNodes.length) {
                val trkptElement = trkptNodes.item(i) as Element
                val lat = trkptElement.getAttribute("lat").toDouble()
                val lon = trkptElement.getAttribute("lon").toDouble()
                
                trackPoints.add(GeoPoint(lat, lon))
                
                // Extract speed from extensions if available
                val extensionsNodes = trkptElement.getElementsByTagName("extensions")
                var speed = 0f
                if (extensionsNodes.length > 0) {
                    val extensionsElement = extensionsNodes.item(0) as Element
                    val speedNodes = extensionsElement.getElementsByTagName("speed")
                    if (speedNodes.length > 0) {
                        speed = speedNodes.item(0).textContent.toFloatOrNull() ?: 0f
                    }
                }
                speeds.add(speed)
            }
            
            return GPXTrack(trackName, trackPoints, speeds)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun getTrackName(document: Document): String {
        val nameNodes = document.getElementsByTagName("name")
        return if (nameNodes.length > 0) {
            nameNodes.item(0).textContent
        } else {
            "Unknown Track"
        }
    }
}