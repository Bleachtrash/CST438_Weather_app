package com.example.weather_app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.model.ForecastPeriod
import com.example.weather_app.ui.weather.WeatherViewModel
import com.google.android.gms.location.LocationServices
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherRoute(
    onSignOut: () -> Unit,
    onOpenFavorites: () -> Unit,
    forcedLocationId: String? = null
) {
    val context = LocalContext.current
    val vm: WeatherViewModel = viewModel()
    val uiState = vm.ui.collectAsState().value

    val isForced = !forcedLocationId.isNullOrBlank()

    LaunchedEffect(forcedLocationId) {
        if (!forcedLocationId.isNullOrBlank()) {
            vm.loadByLocationId(forcedLocationId)
        }
    }

    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasPermission = fine || coarse
    }

    LaunchedEffect(Unit) {
        if (isForced) return@LaunchedEffect

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

    if (!isForced && hasPermission) {
        LocationEffect { lat, lon ->
            vm.load(lat, lon)
        }
    }

    WeatherScreen(
        hasPermission = hasPermission,
        isForced = isForced,
        onRequestPermission = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        },
        city = forcedLocationId ?: uiState.city,
        isLoading = uiState.isLoading,
        error = uiState.error,
        periods = uiState.periods,
        onOpenFavorites = onOpenFavorites,
        onSignOut = onSignOut
    )
}

@SuppressLint("MissingPermission")
@Composable
private fun LocationEffect(onLocation: (Double, Double) -> Unit) {
    val context = LocalContext.current
    var requested by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (requested) return@LaunchedEffect
        requested = true

        val fused = LocationServices.getFusedLocationProviderClient(context)
        fused.lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) onLocation(loc.latitude, loc.longitude)
                else onLocation(36.5975, -121.899)
            }
            .addOnFailureListener {
                onLocation(36.5975, -121.899)
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherScreen(
    hasPermission: Boolean,
    isForced: Boolean,
    onRequestPermission: () -> Unit,
    city: String?,
    isLoading: Boolean,
    error: String?,
    periods: List<ForecastPeriod>,
    onOpenFavorites: () -> Unit,
    onSignOut: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = city ?: "Current Location",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "NOAA forecast",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenFavorites) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!hasPermission && !isForced) {
                PermissionEmptyState(onRequestPermission)
                return@Box
            }

            Crossfade(
                targetState = Triple(isLoading, error, periods),
                label = "weatherCrossfade"
            ) { (loading, err, list) ->
                when {
                    loading -> WeatherSkeleton()
                    err != null -> WeatherErrorState(err)
                    list.isEmpty() -> WeatherErrorState("No forecast data available.")
                    else -> WeatherContent(
                        city = city ?: "Current Location",
                        periods = list,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionEmptyState(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(44.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text("Enable location to load local weather")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRequestPermission) { Text("Grant location permission") }
    }
}

@Composable
private fun WeatherContent(
    city: String,
    periods: List<ForecastPeriod>,
    modifier: Modifier = Modifier
) {
    val current = periods.firstOrNull()
    val hourly = periods.drop(1).take(12)
    val daily = periods.take(14)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { HeroCard(city = city, current = current) }
        if (hourly.isNotEmpty()) {
            item { HourlyRow(hourly) }
        }
        if (daily.isNotEmpty()) {
            items(daily) { p -> ForecastRow(p) }
        }
    }
}

@Composable
private fun HeroCard(city: String, current: ForecastPeriod?) {
    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(city)
            AnimatedContent(targetState = current?.temperature, label = "") { temp ->
                Text(if (temp != null) "${temp}°" else "—")
            }
            Text(current?.shortForecast ?: "—")
        }
    }
}

@Composable
private fun HourlyRow(periods: List<ForecastPeriod>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(periods) { p ->
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.width(96.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(formatTime(p.startTime))
                    Text("${p.temperature}°")
                }
            }
        }
    }
}

@Composable
private fun ForecastRow(p: ForecastPeriod) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(formatDayLabel(p.startTime))
                Text(p.shortForecast)
            }
            Text("${p.temperature}°")
        }
    }
}

@Composable
private fun WeatherSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        repeat(5) {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {}
        }
    }
}

@Composable
private fun WeatherErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Couldn’t load weather")
        Spacer(Modifier.height(8.dp))
        Text(message)
    }
}

private fun formatTime(iso: String): String {
    return try {
        val dt = OffsetDateTime.parse(iso)
        dt.format(DateTimeFormatter.ofPattern("h a", Locale.US))
    } catch (_: Exception) {
        iso
    }
}

private fun formatDayLabel(iso: String): String {
    return try {
        val dt = OffsetDateTime.parse(iso)
        dt.format(DateTimeFormatter.ofPattern("EEEE", Locale.US))
    } catch (_: Exception) {
        iso
    }
}
