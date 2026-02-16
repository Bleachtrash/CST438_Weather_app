package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather_app.data.SessionManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.data.UserRepository
import com.example.weather_app.ui.auth.SignInRoute
import com.example.weather_app.ui.auth.SignInViewModel
import com.example.weather_app.ui.auth.SignInViewModelFactory
import com.example.weather_app.ui.auth.SignUpRoute
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

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = start
                    ) {
                        composable("signin") {
                            val context = LocalContext.current
                            val repo = remember { UserRepository.getInstance(context) }

                            val vm: SignInViewModel = viewModel(
                                factory = SignInViewModelFactory(repo)
                            )

                            SignInRoute(
                                viewModel = vm,
                                onSignedIn = {
                                    navController.navigate("weather") {
                                        popUpTo("signin") { inclusive = true }
                                        launchSingleTop = true
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
}