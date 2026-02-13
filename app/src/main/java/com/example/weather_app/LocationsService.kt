package com.example.weather_app

import androidx.room.Query
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationsService {
    @GET("/postalCodeLookupJSON?username=pgloag")
    suspend fun getLocation(
        @retrofit2.http.Query("placename") placeName: String,
        ) : PostalCodesResponse
}