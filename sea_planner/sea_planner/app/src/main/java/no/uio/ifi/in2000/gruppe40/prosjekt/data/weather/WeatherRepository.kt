package no.uio.ifi.in2000.gruppe40.prosjekt.data.weather

class WeatherRepository(private val api: weatherAPI) {
    suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return try {
            val response = api.getForecast(lat = latitude, lon = longitude)
            Result.success(response)

        } catch (e: Exception){
            Result.failure(e)
        }
    }
}