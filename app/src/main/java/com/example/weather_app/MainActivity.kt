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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_app.network.NoaaApi
import com.example.weather_app.network.NoaaService
import com.example.weather_app.ui.ForecastAdapter
import com.example.weather_app.ui.WeatherViewModel


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
