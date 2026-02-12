package com.example.weather_app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.data.SessionManager
import com.google.android.gms.location.LocationServices

@Composable
fun WeatherRoute(onSignOut: () -> Unit) {
    val context = LocalContext.current

    val vm: WeatherViewModel = viewModel()
    val uiState by vm.ui.collectAsState()

    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = fine || coarse
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasPermission = fineGranted || coarseGranted

        if (!hasPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Weather", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = {
                SessionManager.clear()
                onSignOut()
            }) { Text("Sign out") }
        }

        if (!hasPermission) {
            Text(
                "Location permission is required to load local weather.",
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }) { Text("Grant permission") }
            return@Column
        }

        LocationLoader { lat, lon ->
            vm.load(lat, lon)
        }

        when {
            uiState.isLoading -> {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text(uiState.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                ForecastList(uiState.periods)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun LocationLoader(onLocation: (Double, Double) -> Unit) {
    val context = LocalContext.current
    var requested by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (requested) return@LaunchedEffect
        requested = true

        val fused = LocationServices.getFusedLocationProviderClient(context)
        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    onLocation(loc.latitude, loc.longitude)
                } else {
                    // Emulator fallback
                    onLocation(47.6062, -122.3321)
                }
            }
            .addOnFailureListener {
                onLocation(47.6062, -122.3321)
            }
    }
}

@Composable
private fun ForecastList(periods: List<com.example.weather_app.network.ForecastPeriod>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(periods) { p ->
            Column(Modifier.fillMaxWidth().padding(12.dp)) {
                Text(p.name, style = MaterialTheme.typography.titleMedium)
                Text("${p.temperature}${p.temperatureUnit} — ${p.shortForecast}")
                if (!p.detailedForecast.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(p.detailedForecast!!)
                }
            }
        }
    }
}
