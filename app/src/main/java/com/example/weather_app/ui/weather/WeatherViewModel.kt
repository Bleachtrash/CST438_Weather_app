package com.example.weather_app.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.WeatherRepo
import com.example.weather_app.model.ForecastPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val periods: List<ForecastPeriod> = emptyList(),
    val error: String? = null,
    val city: String? = null
)

class WeatherViewModel(
    private val repo: WeatherRepo = WeatherRepo()
) : ViewModel() {

    private val _ui = MutableStateFlow(WeatherUiState())
    val ui: StateFlow<WeatherUiState> = _ui

    fun load(lat: Double, lon: Double) {
        _ui.value = WeatherUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val periods = repo.getForecastForLatLon(lat, lon)
                _ui.value = WeatherUiState(
                    periods = periods,
                    city = repo.getCityForLatLon(lat, lon)
                )
            } catch (e: Exception) {
                _ui.value = WeatherUiState(error = e.message ?: "Unknown error")
            }
        }
    }

    fun loadByLocationId(locationId: String) {
        when (locationId.trim()) {
            "Monterey County, CA" -> load(36.5975, -121.899)
            "Santa Cruz County, CA" -> load(36.9741, -122.0308)
            "San Benito County, CA" -> load(36.6066, -121.0750)
            "Santa Clara County, CA" -> load(37.3333, -121.9000)
            "San Mateo County, CA" -> load(37.5630, -122.3255)
            "Alameda County, CA" -> load(37.6017, -121.7195)
            "San Francisco County, CA" -> load(37.7749, -122.4194)
            "Contra Costa County, CA" -> load(37.8534, -121.9018)
            "Marin County, CA" -> load(38.0834, -122.7633)
            "Sonoma County, CA" -> load(38.2919, -122.4580)
            else -> load(36.5975, -121.899)
        }
    }
}
