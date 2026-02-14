package com.example.weather_app.data

import com.example.weather_app.data.remote.LocationsService
import com.example.weather_app.data.remote.dto.LocationResponse
import com.example.weather_app.network.LocationsApi

class LocationRepo(
    private val service: LocationsService = LocationsApi.retrofit.create(LocationsService::class.java)
) {
    suspend fun getLatLonFromPlaceName(placeName: String) : List<LocationResponse>{
        val postalCodesResponse = service.getLocation(placeName)
        return postalCodesResponse.postalcodes
    }
}