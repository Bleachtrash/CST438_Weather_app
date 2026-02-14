package com.example.weather_app.data

import com.example.weather_app.data.remote.NoaaService
import com.example.weather_app.model.ForecastPeriod
import com.example.weather_app.network.NoaaApi

class WeatherRepo(
    private val service: NoaaService = NoaaApi.retrofit.create(NoaaService::class.java)
) {
    suspend fun getForecastForLatLon(lat: Double, lon: Double): List<ForecastPeriod> {
        val point = service.getPoint(lat, lon)
        val forecast = service.getForecast(point.properties.forecastHourly)
        return forecast.properties.periods.map {
            ForecastPeriod(
                startTime = it.startTime,
                temperature = it.temperature,
                shortForecast = it.shortForecast
            )
        }
    }

    suspend fun getCityForLatLon(lat: Double, lon: Double): String {
        val point = service.getPoint(lat, lon)
        return point.properties.relativeLocation.properties.city
    }
}
