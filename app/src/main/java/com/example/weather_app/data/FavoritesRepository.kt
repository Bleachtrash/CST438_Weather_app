package com.example.weather_app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class FavoritesRepository (
    private val db: AppDatabase
) {
    private val dao = db.favoriteDao()

    val favoriteLocationIds: Flow<List<String>> =
        SessionManager.currentUserId.flatMapLatest { userId ->
            if (userId == null) flowOf(emptyList())
            else dao.getFavoritesFromUser(userId)
        }

    suspend fun add(locationId: String) {
        val userId = SessionManager.currentUserId.value ?: return
        dao.addFavorite(Favorite(userId, locationId))
    }

    suspend fun remove(locationId: String) {
        val userId = SessionManager.currentUserId.value ?: return
        dao.removeFavorite(userId, locationId)
    }

    suspend fun isFavorite(locationId: String): Boolean {
        val userId = SessionManager.currentUserId.value ?: return false
        return dao.isFavorite(userId, locationId)
    }

    suspend fun toggle(locationId: String) {
        val userId = SessionManager.currentUserId.value ?: return
        if (dao.isFavorite(userId, locationId)) dao.removeFavorite(userId, locationId)
        else dao.addFavorite(Favorite(userId, locationId))

    }

}