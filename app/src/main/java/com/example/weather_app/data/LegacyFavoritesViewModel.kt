package com.example.weather_app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.weather_app.data.local.AppDatabase
import kotlinx.coroutines.launch

class LegacyFavoritesViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Room.databaseBuilder(
        app.applicationContext,
        AppDatabase::class.java,
        "weather_app.db"
    ).build()

    private val repo = FavoritesRepository(db.favoriteDao())

    val favorites = repo.observeFavorites().asLiveData()

    fun remove(locationId: String) = viewModelScope.launch {
        repo.removeFavorite(locationId)
    }
}
