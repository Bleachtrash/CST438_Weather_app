package com.example.weather_app.ui.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.local.AppDatabase

@Composable
fun FavoritesRoute(
    onBack: () -> Unit,
    onOpenFavorite: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(context, AppDatabase::class.java, "weather_app.db").build()
    }
    val repo = remember { FavoritesRepository(db.favoriteDao()) }
    val vm = remember { FavoritesViewModel(repo) }

    FavoritesScreen(
        viewModel = vm,
        onBack = onBack,
        onOpenFavorite = onOpenFavorite
    )
}
