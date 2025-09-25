package no.uio.ifi.in2000.gruppe40.prosjekt.model.grib

import mt.edu.um.cf2.jgribx.GribFile
import java.util.Calendar
import kotlin.math.atan2
import kotlin.math.sqrt

class Wind(
    private val gribFile: GribFile
) {
    // uses java.util.Calendar and not the default kotlin android.Calendar!!!
    // APCP: Total precipitation, UGRD: ucomponent of wind, VGRD: vcomponent of wind
    // Date: relevant to gribfile, typically this day + a pair in the future
    // Ltvid: APCP: Total precipitation, UGRD: ucomponent of wind, VGRD: vcomponent of wind
    // Coordinates between lat: 58.9 to 60.0; lon: 9.8 to 11.2  (dx 0.05)
    // returns precipitation and velocity of wind in two directions


    fun getTotalPrecipitation(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val parameterCode = "APCP"
        val gribrecord = gribFile.getRecord(forecastDate, parameterCode, ltvid)
        val precipitation = gribrecord.getValue(latitude, longitude)

        return precipitation
    }

    private fun getWindUComponent(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val parameterCode = "UGRD"
        val gribrecord = gribFile.getRecord(forecastDate, parameterCode, ltvid)
        val uComponent = gribrecord.getValue(latitude, longitude)

        return uComponent
    }
    
    private fun getWindVComponent(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val parameterCode = "VGRD"
        val gribrecord = gribFile.getRecord(forecastDate, parameterCode, ltvid)
        val vComponent = gribrecord.getValue(latitude, longitude)

        return vComponent
    }

    fun getWindSpeed(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val uComponent = getWindUComponent(forecastDate, ltvid, latitude, longitude)
        val vComponent = getWindVComponent(forecastDate, ltvid, latitude, longitude)

        return sqrt(uComponent * uComponent + vComponent * vComponent)
    }

    fun getWindDirection(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val uComponent = getWindUComponent(forecastDate, ltvid, latitude, longitude)
        val vComponent = getWindVComponent(forecastDate, ltvid, latitude, longitude)

        val theta = Math.toDegrees(atan2(uComponent, vComponent))
        return (theta + 360) % 360
    }
}