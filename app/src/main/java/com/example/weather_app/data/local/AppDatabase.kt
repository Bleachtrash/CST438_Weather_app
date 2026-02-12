package com.example.weather_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weather_app.data.local.FavoriteDao


@Database(
    entities = [FavoriteCountyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
