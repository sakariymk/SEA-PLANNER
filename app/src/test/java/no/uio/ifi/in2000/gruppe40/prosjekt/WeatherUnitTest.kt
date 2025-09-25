package no.uio.ifi.in2000.gruppe40.prosjekt

import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.InstantDetails
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.Next1Hours
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.Properties
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.Summary
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.TimeSeries
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.WeatherData
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.WeatherDetails
import no.uio.ifi.in2000.gruppe40.prosjekt.data.weather.WeatherResponse
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WeatherUnitTest {
    @Test
    fun testWeatherDetails() {
        val currentTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val response = WeatherResponse(
            properties = Properties(
                timeseries = listOf(
                    TimeSeries(
                        time = currentTime,
                        data = WeatherData(
                            instant = InstantDetails(
                                details = WeatherDetails(
                                    air_temperature = 14.5,
                                    wind_speed = 5.2,
                                    wind_from_direction = 180.0
                                )
                            ),
                            next_1_hours = Next1Hours(
                                summary = Summary(
                                    symbol_code = "rainshowers_day"
                                )
                            )
                        )
                    )
                )
            )
        )

        val first = response.properties.timeseries.first()

        Assertions.assertEquals(14.5, first.data.instant.details.air_temperature)
        Assertions.assertEquals(5.2, first.data.instant.details.wind_speed)
        Assertions.assertEquals(180.0, first.data.instant.details.wind_from_direction)
        Assertions.assertEquals("rainshowers_day", first.data.next_1_hours?.summary?.symbol_code)
    }
}
