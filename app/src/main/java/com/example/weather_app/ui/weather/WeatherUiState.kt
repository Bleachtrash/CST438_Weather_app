package com.example.weather_app.ui.weather

import com.example.weather_app.model.ForecastPeriod

data class WeatherUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val periods: List<ForecastPeriod> = emptyList(),
    val city: String? = null
)
