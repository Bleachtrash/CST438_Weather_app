package com.example.weather_app

import com.example.weather_app.network.NoaaApi

class WeatherRepo(
    private val service: NoaaService = NoaaApi.retrofit.create(NoaaService::class.java)
) {
    suspend fun getForecastForLatLon(lat: Double, lon: Double): List<ForecastPeriod> {
        val point = service.getPoint(lat, lon)
        val forecast = service.getForecast(point.properties.forecast)
        return forecast.properties.periods
    }
}