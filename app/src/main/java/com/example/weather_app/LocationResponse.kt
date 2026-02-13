package com.example.weather_app

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostalCodesResponse(
    val postalcodes: List<LocationResponse>
)

@JsonClass(generateAdapter = true)
data class LocationResponse(
    val lat: Double,
    val lng: Double
)