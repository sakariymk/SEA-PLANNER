package no.uio.ifi.in2000.gruppe40.prosjekt.data.weather

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//creates a retrofit object which uses the weatherAPI interface
object WeatherRetrofit {
    private const val BASE_URL = "https://api.met.no"

    val api: weatherAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header("User-Agent", "daniegul@uio.no")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
            .create(weatherAPI::class.java)

    }
}