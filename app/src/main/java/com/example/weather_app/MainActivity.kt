package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.weather_app.data.FavoritesRepository
import com.example.weather_app.data.SessionManager
import com.example.weather_app.data.local.AppDatabase
import com.example.weather_app.ui.LocationRoute
import com.example.weather_app.ui.SignInRoute
import com.example.weather_app.ui.SignUpRoute
import com.example.weather_app.ui.favorites.FavoritesScreen
import com.example.weather_app.ui.favorites.FavoritesViewModel
import com.example.weather_app.ui.theme.Weather_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Weather_AppTheme {
                val navController = rememberNavController()

                val userId by SessionManager.currentUserId.collectAsState()
                val start = if (userId == null) "signin" else "weather"

                NavHost(
                    navController = navController,
                    startDestination = start
                ) {
                    composable("signin") {
                        SignInRoute(
                            onSignedIn = {
                                navController.navigate("weather") {
                                    popUpTo("signin") { inclusive = true }
                                }
                            },
                            onGoToSignUp = { navController.navigate("signup") }

                        )
                    }

                    composable("signup") {
                        SignUpRoute(
                            onSignedUp = {
                                navController.navigate("weather") {
                                    popUpTo("signin") { inclusive = true }
                                }
                            },
                            onGoToSignIn = { navController.popBackStack() }
                        )
                    }

                    composable("weather") {
                        LocationRoute(
                            onSignOut = {
                                SessionManager.clear()
                                navController.navigate("signin") {
                                    popUpTo("weather") { inclusive = true }
                                }
                            }
                        )
                    }
                }
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