package com.example.weather_app.data.remote

import com.example.weather_app.data.remote.dto.PostalCodesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationsService {
    @GET("/postalCodeLookupJSON?username=pgloag")
    suspend fun getLocation(
        @Query("placename") placeName: String,
        ) : PostalCodesResponse
}