package com.example.weather_app.data

import com.example.weather_app.data.local.FavoriteCountyEntity
import com.example.weather_app.data.local.FavoriteDao
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(
    private val dao: FavoriteDao
) {
    fun observeFavorites(): Flow<List<FavoriteCountyEntity>> = dao.observeFavorites()

    suspend fun addFavorite(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        dao.upsert(FavoriteCountyEntity(id = trimmed, name = trimmed))
    }

    suspend fun removeFavorite(id: String) {
        dao.deleteById(id)
    }
}
