package no.uio.ifi.in2000.gruppe40.prosjekt.data.grib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mt.edu.um.cf2.jgribx.GribFile
import no.uio.ifi.in2000.gruppe40.prosjekt.model.grib.ApiEnum
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

//creates an interface which consists of a GET request, a function to return "area", and "content"
interface GribApiService {
    @GET("weatherapi/gribfiles/1.1/")
    suspend fun downloadGrib(
        @Query("area") area: String,
        @Query("content") content: String
    ): ResponseBody
}

// creates a retrofit for the desired api.
val retrofit: Retrofit? = Retrofit.Builder()
    .baseUrl("https://api.met.no/") // Base URL ends with '/'
    .client(
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "dmitrykh@ulrik.uio.no")
                    .build()
                chain.proceed(request)
            }
            .build()
    )
    .build()

//uses the retrofit that was created with the interface
val gribApi = retrofit?.create(GribApiService::class.java)

// returns a gribfile object based on area name (ApiEnum)
suspend fun pullGribFromApiRetrofit(area: ApiEnum): GribFile = withContext(Dispatchers.IO) {
    val areaParams = when (area) {
        ApiEnum.OsloWeather -> Pair("oslofjord", "weather")
        ApiEnum.OsloCurrent -> Pair("oslofjord", "current")
        ApiEnum.OsloWaves -> Pair("oslofjord", "waves")
        ApiEnum.WestWeather -> Pair("west_norway", "weather")
        ApiEnum.WestCurrent -> Pair("west_norway", "current")
        ApiEnum.WestWaves -> Pair("west_norway", "waves")
    }

    val responseBody = gribApi?.downloadGrib(areaParams.first, areaParams.second)

    responseBody?.byteStream().use { inputStream ->
        return@withContext GribFile(inputStream)
    }
}