package com.example.weather_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object SessionManager {
    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId

    fun setUser(userId: Long) {
        _currentUserId.value = userId
    }

    fun clear() {
        _currentUserId.value = null
    }
}