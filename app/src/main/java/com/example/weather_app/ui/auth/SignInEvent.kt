package com.example.weather_app.ui.auth

sealed interface SignInEvent {
    data object SignedIn : SignInEvent
    data class Message(val text: String) : SignInEvent
}
