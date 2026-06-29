package com.example.newapplication.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

// ==========================================
// 1. THE RETROFIT NETWORK OBJECT (The missing definition!)
// ==========================================
object WeatherNetwork {
    private const val BASE_URL = "https://api.weather.gov/"

    val retrofit: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}

// ==========================================
// 2. THE API ENDPOINTS INTERFACE
// ==========================================
interface WeatherApiService {

    // Step 1: Pass lat/lon to get the metadata containing the unique forecast URL
    @Headers("User-Agent: MeteoMarketGameApp (https://github.com/symundy/NewApplication)")
    @GET("points/{lat},{lon}")
    suspend fun getPointData(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double
    ): PointResponse

    // Step 2: Use the exact URL returned by Step 1 to fetch the 7-day forecast array
    @Headers("User-Agent: MeteoMarketGameApp (https://github.com/symundy/NewApplication)")
    @GET
    suspend fun getForecastFromUrl(@Url url: String): ForecastResponse
}

// ==========================================
// 3. THE NWS JSON DATA MODELS
// ==========================================

// Models for Step 1 (/points)
data class PointResponse(val properties: PointProperties)
data class PointProperties(val forecast: String) // This holds the full URL string

// Models for Step 2 (Dynamic Forecast URL)
data class ForecastResponse(val properties: ForecastProperties)
data class ForecastProperties(val periods: List<ForecastPeriod>)
data class ForecastPeriod(
    val startTime: String,  // ISO 8601 format: "2024-06-29T00:00:00-05:00"
    val shortForecast: String,
    val detailedForecast: String,
    val temperature: Int? = null,
    val windSpeed: String? = null
)