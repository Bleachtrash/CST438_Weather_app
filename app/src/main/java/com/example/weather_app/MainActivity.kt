package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather_app.data.SessionManager
import com.example.weather_app.ui.SignInRoute
import com.example.weather_app.ui.SignUpRoute
import com.example.weather_app.ui.WeatherRoute
import com.example.weather_app.ui.favorites.FavoritesRoute
import com.example.weather_app.ui.theme.Weather_AppTheme
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
                        WeatherRoute(
                            onSignOut = {
                                SessionManager.clear()
                                navController.navigate("signin") {
                                    popUpTo("weather") { inclusive = true }
                                }
                            },
                            onOpenFavorites = { navController.navigate("favorites") }
                        )
                    }

                    composable(
                        route = "weatherById/{locationId}",
                        arguments = listOf(
                            navArgument("locationId") { type = NavType.StringType }
                        )
                    ) { entry ->
                        val locationId = URLDecoder.decode(
                            entry.arguments?.getString("locationId") ?: "",
                            StandardCharsets.UTF_8.toString()
                        )

                        WeatherRoute(
                            onSignOut = {
                                SessionManager.clear()
                                navController.navigate("signin") {
                                    popUpTo("weather") { inclusive = true }
                                }
                            },
                            onOpenFavorites = { navController.navigate("favorites") },
                            forcedLocationId = locationId
                        )
                    }

                    composable("favorites") {
                        FavoritesRoute(
                            onBack = { navController.popBackStack() },
                            onOpenFavorite = { locationId ->
                                val safe = URLEncoder.encode(
                                    locationId,
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate("weatherById/$safe")
                            }
                        )
                    }
                }
            }
        }
    }
}
