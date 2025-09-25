package no.uio.ifi.in2000.gruppe40.prosjekt

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.gruppe40.prosjekt.data.grib.GribRepository
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Current
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Waves
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.Wind
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.Calendar

class GribUnitTest {

    @Test
    fun `Adding 1 and 3 should be equal to 4`() {
        Assertions.assertEquals(4 , 1 + 3)
    }

    @Test
    fun testOsloRepository() {
        runBlocking {
            val repository = GribRepository()
            repository.createRepository()

            val osloList: List<Any> = repository.osloList
            Assertions.assertNotEquals(0, osloList.size)
            println("$osloList")
        }
    }

    @Test
    fun testWestNorRepository() {
        runBlocking {
            val repository = GribRepository()
            repository.createRepository()

            val westList = repository.westNorList

            Assertions.assertNotEquals(0, westList.size)
            println("$westList")
        }
    }

    @Test
    fun testOsloComponents() {
        runBlocking {
            val repository = GribRepository()
            repository.createRepository()

            val osloList = repository.osloList

            val osloCurrent = osloList[0] as Current
            val osloWeather = osloList[1] as Wind
            val osloWaves = osloList[2] as Waves

            assertNotNull(osloCurrent)
            assertNotNull(osloWeather)
            assertNotNull(osloWaves)
        }
    }

    @Test
    fun testWestNorComponents() {
        runBlocking {
            val repository = GribRepository()
            repository.createRepository()

            val westNorList = repository.westNorList

            val westNorCurrent = westNorList[0] as Current
            val westNorWeather = westNorList[1] as Wind
            val westNorWaves = westNorList[2] as Waves

            assertNotNull(westNorCurrent)
            assertNotNull(westNorWeather)
            assertNotNull(westNorWaves)
        }
    }


    @Test
    fun testOsloData() {
        runBlocking {
            val repository = GribRepository()
            repository.createRepository()

            val osloList = repository.osloList

            val osloCurrent = osloList.get(0) as Current
            val osloWeather = osloList.get(1) as Wind
            val osloWaves = osloList.get(2) as Waves

            val todayDateAndTime: Calendar = Calendar.getInstance()
            todayDateAndTime.add(Calendar.DAY_OF_MONTH, -1)
            // format -> val forecastDate: Calendar = GregorianCalendar(2025, Calendar.MARCH, 29);

            val latitude = 58.9 //lat: 58.9 to 60.0  (dy 0.05)
            val longitude = 11.2 // lon: 9.8 to 11.2  (dx 0.05)

            val currentSpeed =
                osloCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", latitude, longitude)
            val currentDirection =
                osloCurrent.getCurrentDirection(todayDateAndTime, "DBSL", latitude, longitude)

            assertNotNull(currentSpeed)
            assertNotNull(currentDirection)

            println("Current Speed: $currentSpeed m/s")
            println("Current Direction: $currentDirection o'")

            val waveHeight = osloWaves.getWaveHeight(todayDateAndTime, "TGL", latitude, longitude)

            assertNotNull(waveHeight)

            println("Wave height $waveHeight m")

            val windSpeed = osloWeather.getWindSpeed(todayDateAndTime, "TGL", latitude, longitude)
            val windDirection =
                osloWeather.getWindDirection(todayDateAndTime, "TGL", latitude, longitude)

            assertNotNull(windSpeed)
            assertNotNull(windDirection)

            println("Wind Speed: $windSpeed m/s")
            println("Wind Direction: $windDirection o'")

            // test edge cases
            assertNotNull(osloCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", 58.9, 9.8))
            assertNotNull(osloCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", 60.0, 11.2))

            // test bad coordinates
            assertNotNull(osloCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", 10.0, 10.0))
        }
    }

    @Test
    fun testWestNorData() {
        runBlocking {
            val repository = GribRepository()
            repository.createRepository()

            val westNorList = repository.westNorList

            val westNorCurrent = westNorList.get(0) as Current
            val westNorWind = westNorList.get(1) as Wind
            val westNorWaves = westNorList.get(2) as Waves

            val todayDateAndTime: Calendar = Calendar.getInstance()
            todayDateAndTime.add(Calendar.DAY_OF_MONTH, -1)
            // format -> val forecastDate: Calendar = GregorianCalendar(2025, Calendar.MARCH, 29);
            // Koordinater WestNor: LatLon Grid  (52x103) lat: 57.9 to 63.0  (dy 0.05) lon: 4.45 to 7.0  (dx 0.05)

            val latitude = 58.10
            val longitude = 4.50

            val currentSpeed =
                westNorCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", latitude, longitude)
            val currentDirection =
                westNorCurrent.getCurrentDirection(todayDateAndTime, "DBSL", latitude, longitude)

            assertNotNull(currentSpeed)
            assertNotNull(currentDirection)

            println("Current Speed: $currentSpeed m/s")
            println("Current Direction: $currentDirection o'")

            val waveHeight =
                westNorWaves.getWaveHeight(todayDateAndTime, "TGL", latitude, longitude)

            assertNotNull(waveHeight)

            println("Wave height $waveHeight m")

            val windSpeed = westNorWind.getWindSpeed(todayDateAndTime, "TGL", latitude, longitude)
            val windDirection =
                westNorWind.getWindDirection(todayDateAndTime, "TGL", latitude, longitude)

            assertNotNull(windSpeed)
            assertNotNull(windDirection)

            println("Wind Speed: $windSpeed m/s")
            println("Wind Direction: $windDirection o'")

            // test edge cases
            assertNotNull(westNorCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", 57.9, 4.45))
            assertNotNull(westNorCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", 63.0, 7.0))

            // test bad coordinates
            assertNotNull(westNorCurrent.getCurrentSpeed(todayDateAndTime, "DBSL", 10.0, 10.0))
        }
    }
}
