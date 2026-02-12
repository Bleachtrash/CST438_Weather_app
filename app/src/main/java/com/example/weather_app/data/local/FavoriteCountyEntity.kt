package com.example.weather_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_counties")
data class FavoriteCountyEntity(
    @PrimaryKey val id: String,
    val name: String
)