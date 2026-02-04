package com.example.weather_app.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.local.FavoriteCountyEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesViewModel(
    private val repo: FavoritesRepository
) : ViewModel() {
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    fun toggleEditing() {
        _isEditing.value = !_isEditing.value
    }

    fun setEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    val favorites: StateFlow<List<FavoriteCountyEntity>> =
        repo.observeFavorites()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addFavorite(name: String) {
        viewModelScope.launch { repo.addFavorite(name) }
    }

    fun removeFavorite(id: String) {
        viewModelScope.launch { repo.removeFavorite(id) }
    }

    class Factory(private val repo: FavoritesRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoritesViewModel(repo) as T
        }
    }
}
