package com.example.weather_app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BlueSecondary,
    onPrimary = Color.White,

    secondary = Accent,
    onSecondary = Color.Black,

    background = DarkSurface,
    onBackground = Color.White,

    surface = DarkSurfaceVariant,
    onSurface = Color.White,

    surfaceVariant = DarkSurface,
    onSurfaceVariant = Color(0xFFCBD5E1)
)

@Composable
fun Weather_AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
