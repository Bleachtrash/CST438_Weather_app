package com.example.weather_app.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.WeatherRepo
import com.example.weather_app.data.remote.dto.ForecastPeriod
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
                _ui.value = WeatherUiState(periods = periods, city = repo.getCityForLatLon(lat, lon))
            } catch (e: Exception) {
                _ui.value = WeatherUiState(error = e.message ?: "Unknown error")
            }
        }
    }
}