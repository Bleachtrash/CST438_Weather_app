package com.example.weather_app.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface NoaaService {

    @GET("points/{lat},{lon}")
    suspend fun getPoint(
        @Path("lat") lat: Double,
        @Path("lon") lon: Double
    ): PointResponse

    @GET
    suspend fun getForecast(@Url forecastUrl: String): ForecastResponse
}
