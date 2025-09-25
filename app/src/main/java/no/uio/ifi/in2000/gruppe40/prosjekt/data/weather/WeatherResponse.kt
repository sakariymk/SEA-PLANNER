package no.uio.ifi.in2000.gruppe40.prosjekt.data.weather


data class WeatherResponse (
    val properties: Properties
)

data class Properties(
    val timeseries: List<TimeSeries>
)

data class TimeSeries(
    val time: String,
    val data: WeatherData
)

data class WeatherData(
    val instant: InstantDetails,
    val next_1_hours: Next1Hours?
)

data class InstantDetails(
    val details: WeatherDetails
)

data class Next1Hours(
    val summary: Summary
)

data class Summary(
    val symbol_code: String
)
data class WeatherDetails(
    val air_temperature: Double,
    val wind_speed: Double,
    val wind_from_direction: Double
)
