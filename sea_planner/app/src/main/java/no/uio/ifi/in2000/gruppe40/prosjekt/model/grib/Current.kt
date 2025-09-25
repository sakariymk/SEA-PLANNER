package no.uio.ifi.in2000.gruppe40.prosjekt.model.grib

import mt.edu.um.cf2.jgribx.GribFile
import java.util.Calendar
import kotlin.math.atan2
import kotlin.math.sqrt

class Current(
    private val gribFile: GribFile
) {
    // Date: relevant to gribfile, typically shows this day + a pair in the future
    // ltvid: ONLY DBSL, or DBSL:3
    // ParameterCode: ONLY UOGRD and VOGRD!!!! Ex: UOGRD/VOGRD or UOGRD/VOGRD:10, but must be UOGRD or VOGRD
    // Coordinates Oslo: lat: 58.9 to 60.0; lon; 9.8 to 11.2  (dx 0.05)
    // Coordinates WestNor: LatLon Grid  (52x103) lat: 57.9 to 63.0  (dy 0.05) lon: 4.45 to 7.0  (dx 0.05)
    // returns speed in two directions, of which we can calculate both direction and velocity

    private fun getCurrentUComponent(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val parameterCode = "UOGRD"
        val gribRecord = gribFile.getRecord(forecastDate, parameterCode, ltvid)
        val uComponent = gribRecord.getValue(latitude, longitude)

        return uComponent
    }

    private fun getCurrentVComponent(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val parameterCode = "VOGRD"
        val gribRecord = gribFile.getRecord(forecastDate, parameterCode, ltvid)
        val vComponent = gribRecord.getValue(latitude, longitude)

        return vComponent
    }

    fun getCurrentSpeed(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val uComponent = getCurrentUComponent(forecastDate, ltvid, latitude, longitude)
        val vComponent = getCurrentVComponent(forecastDate, ltvid, latitude, longitude)

        return sqrt(uComponent * uComponent + vComponent * vComponent)
    }

    fun getCurrentDirection(forecastDate: Calendar, ltvid: String, latitude: Double, longitude: Double): Double {
        val uComponent = getCurrentUComponent(forecastDate, ltvid, latitude, longitude)
        val vComponent = getCurrentVComponent(forecastDate, ltvid, latitude, longitude)

        val theta = Math.toDegrees(atan2(uComponent, vComponent))
        return (theta + 360) % 360
    }

}