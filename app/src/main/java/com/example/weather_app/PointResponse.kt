package com.example.weather_app

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PointResponse(
    val properties: PointProperties
)

@JsonClass(generateAdapter = true)
data class PointProperties(
    val forecast: String,
    val forecastHourly: String? = null
)