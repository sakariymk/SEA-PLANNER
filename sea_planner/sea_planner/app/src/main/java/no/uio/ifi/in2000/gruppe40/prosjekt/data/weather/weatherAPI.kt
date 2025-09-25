package no.uio.ifi.in2000.gruppe40.prosjekt.data.weather

import retrofit2.http.GET
import retrofit2.http.Query

//creates an interface which consists of a GET request, a function to return "lat" and "lon"
interface weatherAPI {
    @GET("weatherapi/locationforecast/2.0/compact")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse
}

