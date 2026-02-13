package com.example.weather_app.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PointResponse(
    val properties: PointProperties
)

@JsonClass(generateAdapter = true)
data class PointProperties(
    val forecast: String,
    val forecastHourly: String,
    val relativeLocation: RelativeLocation
)

@JsonClass(generateAdapter = true)
data class RelativeLocation(
    val properties: RelativeLocationProperties
)

@JsonClass(generateAdapter = true)
data class RelativeLocationProperties(
    val city: String
)

