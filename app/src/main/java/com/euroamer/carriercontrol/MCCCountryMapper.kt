package com.euroamer.carriercontrol

/**
 * Utility class to map Mobile Country Codes (MCC) to country names
 */
object MCCCountryMapper {
    
    // Map of MCC codes to country names
    private val mccToCountry = mapOf(
        "232" to "Austria",
        "206" to "Belgium",
        "284" to "Bulgaria",
        "219" to "Croatia",
        "280" to "Cyprus",
        "230" to "Czech Republic",
        "238" to "Denmark",
        "248" to "Estonia",
        "244" to "Finland",
        "208" to "France",
        "262" to "Germany",
        "202" to "Greece",
        "216" to "Hungary",
        "272" to "Ireland",
        "222" to "Italy",
        "247" to "Latvia",
        "246" to "Lithuania",
        "270" to "Luxembourg",
        "278" to "Malta",
        "204" to "Netherlands",
        "260" to "Poland",
        "268" to "Portugal",
        "226" to "Romania",
        "231" to "Slovakia",
        "293" to "Slovenia",
        "214" to "Spain",
        "240" to "Sweden"
    )
    
    /**
     * Get country name from MCC code
     * @param mcc Mobile Country Code
     * @return Country name or null if not found
     */
    fun getCountryFromMCC(mcc: String): String? {
        return mccToCountry[mcc]
    }
    
    /**
     * Check if MCC code belongs to an EU country
     * @param mcc Mobile Country Code
     * @return true if EU country, false otherwise
     */
    fun isEUCountry(mcc: String): Boolean {
        return mccToCountry.containsKey(mcc)
    }
}