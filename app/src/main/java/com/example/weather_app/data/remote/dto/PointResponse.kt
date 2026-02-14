package com.example.weather_app.data.remote.dto

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

