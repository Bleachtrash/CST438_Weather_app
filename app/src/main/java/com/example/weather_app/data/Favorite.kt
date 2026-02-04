package com.example.weather_app.data

import androidx.room.Entity


@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "locationId"]
)
data class Favorite(
    val userId: Long,
    val locationId: String
)