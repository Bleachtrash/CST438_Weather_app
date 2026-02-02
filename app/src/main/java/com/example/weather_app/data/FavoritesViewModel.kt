package com.example.weather_app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FavoritesViewModel (app: Application) : AndroidViewModel(app){
    private val db = AppDatabase.getInstance(app)
    private val repo = FavoritesRepository(db)

    val favorites = repo.favoriteLocationIds.asLiveData()

    fun remove(locationId: String) = viewModelScope.launch {
        repo.remove(locationId)
    }


}