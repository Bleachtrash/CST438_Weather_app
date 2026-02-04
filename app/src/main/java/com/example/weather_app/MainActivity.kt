package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.local.AppDatabase
import com.example.weather_app.ui.favorites.FavoritesScreen
import com.example.weather_app.ui.favorites.FavoritesViewModel
import com.example.weather_app.ui.theme.Weather_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Weather_AppTheme {
                val db = remember {
                    Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "weather_app.db"
                    ).build()
                }

                val repo = remember { FavoritesRepository(db.favoriteDao()) }

                val vm: FavoritesViewModel = viewModel(
                    factory = FavoritesViewModel.Factory(repo)
                )

                FavoritesScreen(
                    viewModel = vm,
                    onOpenFavorite = { favoriteId: String ->
                        // TODO: handle favorite click (navigate, load forecast, etc.)
                    }
                )
            }
        }
    }
}
