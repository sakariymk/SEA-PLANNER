package no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert

import retrofit2.http.GET
import retrofit2.http.Query

//Retrofit interface for accessing Metalert API
interface MetalertAPI {
    //Makes a GET request to the "current.json" endpoint consisting of the current warnings
    @GET("current.json")
    suspend fun getMetalerts(
        //Parameter/filter specifying that the domain of interest is "marine"
        @Query("geographicDomain") domain: String = "marine"
    ): MetalertResponse //The response is mapped to the MetalertResponse data class
}