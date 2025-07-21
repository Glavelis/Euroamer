package com.euroamer.carriercontrol

import org.osmdroid.util.GeoPoint

class EUBoundaryChecker {
    
    // Simplified EU boundary coordinates (approximate bounding box)
    // In a production app, you would use more precise polygon data
    private val euBoundaries = mapOf(
        "Austria" to listOf(
            GeoPoint(49.0, 9.5),
            GeoPoint(49.0, 17.2),
            GeoPoint(46.4, 17.2),
            GeoPoint(46.4, 9.5)
        ),
        "Belgium" to listOf(
            GeoPoint(51.5, 2.5),
            GeoPoint(51.5, 6.4),
            GeoPoint(49.5, 6.4),
            GeoPoint(49.5, 2.5)
        ),
        "Bulgaria" to listOf(
            GeoPoint(44.2, 22.4),
            GeoPoint(44.2, 28.6),
            GeoPoint(41.2, 28.6),
            GeoPoint(41.2, 22.4)
        ),
        "Croatia" to listOf(
            GeoPoint(46.5, 13.5),
            GeoPoint(46.5, 19.4),
            GeoPoint(42.4, 19.4),
            GeoPoint(42.4, 13.5)
        ),
        "Cyprus" to listOf(
            GeoPoint(35.7, 32.3),
            GeoPoint(35.7, 34.6),
            GeoPoint(34.6, 34.6),
            GeoPoint(34.6, 32.3)
        ),
        "Czech Republic" to listOf(
            GeoPoint(51.1, 12.1),
            GeoPoint(51.1, 18.9),
            GeoPoint(48.6, 18.9),
            GeoPoint(48.6, 12.1)
        ),
        "Denmark" to listOf(
            GeoPoint(57.8, 8.1),
            GeoPoint(57.8, 15.2),
            GeoPoint(54.6, 15.2),
            GeoPoint(54.6, 8.1)
        ),
        "Estonia" to listOf(
            GeoPoint(59.7, 21.8),
            GeoPoint(59.7, 28.2),
            GeoPoint(57.5, 28.2),
            GeoPoint(57.5, 21.8)
        ),
        "Finland" to listOf(
            GeoPoint(70.1, 20.6),
            GeoPoint(70.1, 31.6),
            GeoPoint(59.8, 31.6),
            GeoPoint(59.8, 20.6)
        ),
        "France" to listOf(
            GeoPoint(51.1, -5.1),
            GeoPoint(51.1, 9.6),
            GeoPoint(41.3, 9.6),
            GeoPoint(41.3, -5.1)
        ),
        "Germany" to listOf(
            GeoPoint(55.1, 5.9),
            GeoPoint(55.1, 15.0),
            GeoPoint(47.3, 15.0),
            GeoPoint(47.3, 5.9)
        ),
        "Greece" to listOf(
            GeoPoint(41.7, 19.4),
            GeoPoint(41.7, 28.2),
            GeoPoint(34.8, 28.2),
            GeoPoint(34.8, 19.4)
        ),
        "Hungary" to listOf(
            GeoPoint(48.6, 16.1),
            GeoPoint(48.6, 22.9),
            GeoPoint(45.7, 22.9),
            GeoPoint(45.7, 16.1)
        ),
        "Ireland" to listOf(
            GeoPoint(55.4, -10.5),
            GeoPoint(55.4, -6.0),
            GeoPoint(51.4, -6.0),
            GeoPoint(51.4, -10.5)
        ),
        "Italy" to listOf(
            GeoPoint(47.1, 6.6),
            GeoPoint(47.1, 18.5),
            GeoPoint(35.5, 18.5),
            GeoPoint(35.5, 6.6)
        ),
        "Latvia" to listOf(
            GeoPoint(58.1, 21.0),
            GeoPoint(58.1, 28.2),
            GeoPoint(55.7, 28.2),
            GeoPoint(55.7, 21.0)
        ),
        "Lithuania" to listOf(
            GeoPoint(56.4, 20.9),
            GeoPoint(56.4, 26.8),
            GeoPoint(53.9, 26.8),
            GeoPoint(53.9, 20.9)
        ),
        "Luxembourg" to listOf(
            GeoPoint(50.2, 5.7),
            GeoPoint(50.2, 6.5),
            GeoPoint(49.4, 6.5),
            GeoPoint(49.4, 5.7)
        ),
        "Malta" to listOf(
            GeoPoint(36.1, 14.2),
            GeoPoint(36.1, 14.6),
            GeoPoint(35.8, 14.6),
            GeoPoint(35.8, 14.2)
        ),
        "Netherlands" to listOf(
            GeoPoint(53.6, 3.4),
            GeoPoint(53.6, 7.2),
            GeoPoint(50.8, 7.2),
            GeoPoint(50.8, 3.4)
        ),
        "Poland" to listOf(
            GeoPoint(54.8, 14.1),
            GeoPoint(54.8, 24.1),
            GeoPoint(49.0, 24.1),
            GeoPoint(49.0, 14.1)
        ),
        "Portugal" to listOf(
            GeoPoint(42.2, -9.5),
            GeoPoint(42.2, -6.2),
            GeoPoint(36.9, -6.2),
            GeoPoint(36.9, -9.5)
        ),
        "Romania" to listOf(
            GeoPoint(48.3, 20.3),
            GeoPoint(48.3, 29.7),
            GeoPoint(43.6, 29.7),
            GeoPoint(43.6, 20.3)
        ),
        "Slovakia" to listOf(
            GeoPoint(49.6, 16.8),
            GeoPoint(49.6, 22.6),
            GeoPoint(47.7, 22.6),
            GeoPoint(47.7, 16.8)
        ),
        "Slovenia" to listOf(
            GeoPoint(46.9, 13.4),
            GeoPoint(46.9, 16.6),
            GeoPoint(45.4, 16.6),
            GeoPoint(45.4, 13.4)
        ),
        "Spain" to listOf(
            GeoPoint(43.8, -9.3),
            GeoPoint(43.8, 4.3),
            GeoPoint(35.2, 4.3),
            GeoPoint(35.2, -9.3)
        ),
        "Sweden" to listOf(
            GeoPoint(69.1, 11.1),
            GeoPoint(69.1, 24.2),
            GeoPoint(55.3, 24.2),
            GeoPoint(55.3, 11.1)
        )
    )
    
    fun isLocationInEU(location: GeoPoint): Pair<Boolean, String?> {
        for ((country, boundaries) in euBoundaries) {
            if (isPointInBoundingBox(location, boundaries)) {
                return Pair(true, country)
            }
        }
        return Pair(false, null)
    }
    
    private fun isPointInBoundingBox(point: GeoPoint, boundingBox: List<GeoPoint>): Boolean {
        if (boundingBox.size < 4) return false
        
        val minLat = boundingBox.minOf { it.latitude }
        val maxLat = boundingBox.maxOf { it.latitude }
        val minLon = boundingBox.minOf { it.longitude }
        val maxLon = boundingBox.maxOf { it.longitude }
        
        return point.latitude >= minLat && point.latitude <= maxLat &&
               point.longitude >= minLon && point.longitude <= maxLon
    }
    
    fun getEUBoundaryPolygons(): List<Pair<String, List<GeoPoint>>> {
        return euBoundaries.toList()
    }
}