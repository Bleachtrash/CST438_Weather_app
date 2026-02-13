package com.example.weather_app

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    val properties: ForecastProperties
)

@JsonClass(generateAdapter = true)
data class ForecastProperties(
    val periods: List<ForecastPeriod>
)

@JsonClass(generateAdapter = true)
data class ForecastPeriod(
    val name: String,
    val startTime: String,
    val endTime: String,
    val temperature: Int,
    val temperatureUnit: String,
    val shortForecast: String,
    val detailedForecast: String? = null
)