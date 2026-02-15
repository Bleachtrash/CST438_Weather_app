package com.example.weather_app.ui.favorites

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.local.AppDatabase

class FavoritesPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val db = remember {
                Room.databaseBuilder(context, AppDatabase::class.java, "weather_app.db").build()
            }
            val repo = remember { FavoritesRepository(db.favoriteDao()) }
            val vm = remember { FavoritesViewModel(repo) }

            FavoritesScreen(
                viewModel = vm,
                onOpenFavorite = { _: String -> }
            )
        }
    }
}
