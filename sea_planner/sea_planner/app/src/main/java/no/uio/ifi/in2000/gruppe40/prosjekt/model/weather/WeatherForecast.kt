package no.uio.ifi.in2000.gruppe40.prosjekt.model.weather

//for WeatherScreen
data class WeatherForecast(
    val date: String,
    val temperature: Double,
    val symbolCode: String,
    val windSpeed: Double,
    val windDirection: Double
)