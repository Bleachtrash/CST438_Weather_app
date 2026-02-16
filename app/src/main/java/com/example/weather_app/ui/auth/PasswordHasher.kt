package com.example.weather_app.ui.auth

import java.security.MessageDigest

object PasswordHasher {
    fun sha256(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(password.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
