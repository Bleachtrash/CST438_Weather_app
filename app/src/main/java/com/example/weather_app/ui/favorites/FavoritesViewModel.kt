package com.example.weather_app.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.local.FavoriteCountyEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repo: FavoritesRepository
) : ViewModel() {

    val favorites: StateFlow<List<FavoriteCountyEntity>> =
        repo.observeFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
        if (!_isEditing.value) clearQuery()
    }

    fun addFavorite(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            repo.addFavorite(trimmed)
        }
    }

    fun removeFavorite(id: String) {
        val trimmed = id.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            repo.removeFavorite(trimmed)
        }
    }

    fun clearQuery() {
        _suggestions.value = emptyList()
    }
}
