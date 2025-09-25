package no.uio.ifi.in2000.gruppe40.prosjekt.data.grib

import android.util.Log
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.ApiEnum
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Current
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Waves
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Wind

class GribRepository {
    //variables could be objects
    var osloList: List<Any> = emptyList()
    var westNorList: List<Any> = emptyList()

    suspend fun createRepository() {
        try {
            val osloCurrent = Current(pullGribFromApiRetrofit(ApiEnum.OsloCurrent))
            val osloWind = Wind(pullGribFromApiRetrofit(ApiEnum.OsloWeather))
            val osloWaves = Waves(pullGribFromApiRetrofit(ApiEnum.OsloWaves))

            osloList = listOf(osloCurrent, osloWind, osloWaves)

            val westNorCurrent = Current(pullGribFromApiRetrofit(ApiEnum.WestCurrent))
            val westNorWeather = Wind(pullGribFromApiRetrofit(ApiEnum.WestWeather))
            val westNorWaves = Waves(pullGribFromApiRetrofit(ApiEnum.WestWaves))

            westNorList = listOf(westNorCurrent, westNorWeather, westNorWaves)
        } catch (e: Exception) {
            Log.e("Grib", "GribRepository could not be created")
        }
    }
}
