package com.example.weather_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT locationId FROM favorites WHERE userId = :userId")
    fun getFavoritesFromUser(userId: Long): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND locationId = :locationId)")
    suspend fun isFavorite(userId: Long, locationId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    @Query ("DELETE FROM favorites WHERE userId = :userId AND locationId = :locationId")
    suspend fun removeFavorite(userId: Long, locationId: String)


}