package com.example.weather_app

import com.example.weather_app.network.LocationsApi

class LocationRepo(
    private val service: LocationsService = LocationsApi.retrofit.create(LocationsService::class.java)
) {
    suspend fun getLatLonFromPlaceName(placeName: String) : List<LocationResponse>{
        val postalCodesResponse = service.getLocation(placeName)
        return postalCodesResponse.postalcodes
    }
}