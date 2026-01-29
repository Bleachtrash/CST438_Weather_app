package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.local.AppDatabase
import com.example.weather_app.ui.favorites.FavoritesScreen
import com.example.weather_app.ui.favorites.FavoritesViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.weather_app.ui.theme.Weather_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Isaiah: Made room verification, but removed it here.
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

                val vm: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory(repo))

                FavoritesScreen(
                    viewModel = vm,
                    onOpenFavorite = { favoriteId ->

                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Weather_AppTheme {
        Greeting("Android")
    }
}