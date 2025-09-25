package no.uio.ifi.in2000.gruppe40.prosjekt.model.grib

import mt.edu.um.cf2.jgribx.GribFile
import java.util.Calendar

class Waves(
    private val gribFile: GribFile
) {
    // Date: relevant to gribfile, typically this day + a pair in the future
    // Ltvid: ONLY TGL!!!! Ex: TGL or TGL:10, but must be TGL
    // Coordinates between lat: 58.9 to 60.0; lon: 9.8 to 11.2  (dx 0.05)
    // returns significant height of combined wind waves and swell

    fun getWaveHeight(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val parameterCode = "HTSGW"
        val gribrecord = gribFile.getRecord(forecastDate, parameterCode, ltvid)
        val waveHeight = gribrecord.getValue(latitude, longitude)

        return waveHeight
    }
}