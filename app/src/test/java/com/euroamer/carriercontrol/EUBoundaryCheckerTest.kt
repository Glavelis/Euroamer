package com.euroamer.carriercontrol

import org.junit.Test
import org.junit.Assert.*
import org.osmdroid.util.GeoPoint

class EUBoundaryCheckerTest {
    
    private val euBoundaryChecker = EUBoundaryChecker()
    
    @Test
    fun testLocationInGermany() {
        val berlinLocation = GeoPoint(52.5200, 13.4050) // Berlin, Germany
        val (isInEU, country) = euBoundaryChecker.isLocationInEU(berlinLocation)
        assertTrue("Berlin should be in EU", isInEU)
        assertEquals("Should be in Germany", "Germany", country)
    }
    
    @Test
    fun testLocationInFrance() {
        val parisLocation = GeoPoint(48.8566, 2.3522) // Paris, France
        val (isInEU, country) = euBoundaryChecker.isLocationInEU(parisLocation)
        assertTrue("Paris should be in EU", isInEU)
        assertEquals("Should be in France", "France", country)
    }
    
    @Test
    fun testLocationOutsideEU() {
        val newYorkLocation = GeoPoint(40.7128, -74.0060) // New York, USA
        val (isInEU, country) = euBoundaryChecker.isLocationInEU(newYorkLocation)
        assertFalse("New York should not be in EU", isInEU)
        assertNull("Country should be null", country)
    }
    
    @Test
    fun testLocationInSpain() {
        val madridLocation = GeoPoint(40.4168, -3.7038) // Madrid, Spain
        val (isInEU, country) = euBoundaryChecker.isLocationInEU(madridLocation)
        assertTrue("Madrid should be in EU", isInEU)
        assertEquals("Should be in Spain", "Spain", country)
    }
}