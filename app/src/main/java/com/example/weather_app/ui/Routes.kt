package com.example.weather_app.ui

import androidx.navigation.NavController
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val SIGN_IN = "signin"
    const val SIGN_UP = "signup"
    const val WEATHER = "weather"
    const val FAVORITES = "favorites"

    const val WEATHER_BY_ID = "weatherById/{locationId}"
    fun weatherById(locationId: String): String = "weatherById/${locationId.encodeForRoute()}"
}

fun NavController.navigateToSignIn(clearFrom: String? = null) {
    navigate(Routes.SIGN_IN) {
        launchSingleTop = true
        if (clearFrom != null) {
            popUpTo(clearFrom) { inclusive = true }
        }
    }
}

fun NavController.navigateToWeather(clearFrom: String? = null) {
    navigate(Routes.WEATHER) {
        launchSingleTop = true
        if (clearFrom != null) {
            popUpTo(clearFrom) { inclusive = true }
        }
    }
}

fun NavController.navigateToSignUp() {
    navigate(Routes.SIGN_UP) {
        launchSingleTop = true
    }
}

fun NavController.navigateToFavorites() {
    navigate(Routes.FAVORITES) {
        launchSingleTop = true
    }
}

fun NavController.navigateToWeatherById(locationId: String) {
    navigate(Routes.weatherById(locationId)) {
        launchSingleTop = true
    }
}

fun String.encodeForRoute(): String =
    URLEncoder.encode(this, StandardCharsets.UTF_8.toString())

fun String.decodeFromRoute(): String =
    URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
