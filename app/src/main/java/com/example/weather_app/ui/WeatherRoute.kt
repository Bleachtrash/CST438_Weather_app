package com.example.weather_app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.model.ForecastPeriod
import com.example.weather_app.ui.weather.WeatherHero
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
        LocationEffect { lat, lon -> vm.load(lat, lon) }
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
                        Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
                    }
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Sign out")
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

    val hl = remember(periods) { computeTodayHighLow(periods) }
    val highText = hl.high?.let { "${it}°" } ?: "—"
    val lowText = hl.low?.let { "${it}°" } ?: "—"

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ElevatedCard(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                WeatherHero(
                    location = city,
                    temperature = current?.temperature?.let { "${it}°" } ?: "—",
                    condition = current?.shortForecast ?: "—",
                    high = highText,
                    low = lowText,
                    icon = emojiForForecast(current?.shortForecast)
                )
            }
        }

        item {
            WeatherStatsRow(
                wind = "—",
                humidity = "—",
                uv = "—",
                updated = current?.startTime?.let { formatTime(it) } ?: "—"
            )
        }

        if (hourly.isNotEmpty()) {
            item { HourlyRow(hourly) }
        }

        if (daily.isNotEmpty()) {
            items(items = daily) { p ->
                ForecastRow(p)
            }
        }
    }
}

@Composable
private fun WeatherStatsRow(
    wind: String,
    humidity: String,
    uv: String,
    updated: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatChip(label = "Wind", value = wind, modifier = Modifier.weight(1f))
        StatChip(label = "Humidity", value = humidity, modifier = Modifier.weight(1f))
        StatChip(label = "UV", value = uv, modifier = Modifier.weight(1f))
        StatChip(label = "Updated", value = updated, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(value, style = MaterialTheme.typography.titleSmall)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HourlyRow(periods: List<ForecastPeriod>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(periods) { p ->
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.width(104.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatTime(p.startTime),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = emojiForForecast(p.shortForecast),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${p.temperature}°",
                        style = MaterialTheme.typography.titleMedium
                    )
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
            Text(
                text = emojiForForecast(p.shortForecast),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDayLabel(p.startTime),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = p.shortForecast,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${p.temperature}°",
                style = MaterialTheme.typography.titleMedium
            )
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
                    .height(72.dp)
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
        Text("Couldn’t load weather", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun emojiForForecast(text: String?): String {
    val t = (text ?: "").lowercase()
    return when {
        "thunder" in t -> "⛈️"
        "snow" in t || "sleet" in t || "blizzard" in t -> "❄️"
        "rain" in t || "showers" in t || "drizzle" in t -> "🌧️"
        "fog" in t || "haze" in t || "smoke" in t -> "🌫️"
        "wind" in t -> "💨"
        "cloud" in t || "overcast" in t -> "☁️"
        "sun" in t || "clear" in t -> "☀️"
        else -> "🌤️"
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

private data class HighLow(val high: Int?, val low: Int?)

private fun computeTodayHighLow(periods: List<ForecastPeriod>): HighLow {
    val first = periods.firstOrNull() ?: return HighLow(null, null)

    val firstDt = runCatching { OffsetDateTime.parse(first.startTime) }.getOrNull()
        ?: return HighLow(null, null)

    val day = firstDt.toLocalDate()

    val todaysTemps = periods.mapNotNull { p ->
        val dt = runCatching { OffsetDateTime.parse(p.startTime) }.getOrNull() ?: return@mapNotNull null
        if (dt.toLocalDate() == day) p.temperature else null
    }

    if (todaysTemps.isEmpty()) return HighLow(null, null)

    return HighLow(
        high = todaysTemps.maxOrNull(),
        low = todaysTemps.minOrNull()
    )
}
