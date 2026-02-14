package com.example.weather_app.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.LocationRepo
import com.example.weather_app.data.remote.dto.LocationResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LocationsUiState(
    val isLoading: Boolean = false,
    val postalCodes: List<LocationResponse> = emptyList(),
    val error: String? = null
)

class LocationViewModel(
    private val repo: LocationRepo = LocationRepo()
) : ViewModel() {
    private val _ui = MutableStateFlow(LocationsUiState())
    val ui: StateFlow<LocationsUiState> = _ui

    fun load(placeName: String){
        _ui.value = LocationsUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val location = repo.getLatLonFromPlaceName(placeName)
                _ui.value = LocationsUiState(postalCodes = location)
                println("HELLO??")
            } catch (e: Exception){
                _ui.value = LocationsUiState(error = e.message ?: "Unknown error")
                println(e.message?: "Unknown error")
            }
        }
    }
}