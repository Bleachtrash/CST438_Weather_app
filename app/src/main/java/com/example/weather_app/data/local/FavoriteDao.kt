package com.example.weather_app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_counties ORDER BY name ASC")
    fun observeFavorites(): Flow<List<FavoriteCountyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(favorite: FavoriteCountyEntity)

    @Query("DELETE FROM favorite_counties WHERE id = :id")
    suspend fun deleteById(id: String)
}