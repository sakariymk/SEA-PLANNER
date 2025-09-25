package no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

//Creates and configures a Retrofit instance for MetalertAPI
object MetalertRetrofit {
    private const val BASE_URL = "https://api.met.no/weatherapi/metalerts/2.0/"

    //HTTP logging interceptor, debugging purposes
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //OkHttpClient with logging and defines timeouts
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS) //Max time to establish connection
        .writeTimeout(30, TimeUnit.SECONDS) //Max time to send request
        .readTimeout(30, TimeUnit.SECONDS) // Max time to read response
        .build()

    //Retrofit API client
    val api: MetalertAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //Converts JSON to Kotlin objects
            .client(client)
            .build()
            .create(MetalertAPI::class.java)
    }
}

