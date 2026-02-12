package com.example.weather_app.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

@Composable
fun LocationRoute(
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    var hasPermission by remember { mutableStateOf(false) }
    var latLonText by remember { mutableStateOf("No location yet") }
    var error by remember { mutableStateOf<String?>(null) }

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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Location (Wiring Step)", style = MaterialTheme.typography.headlineMedium)

        if (!hasPermission) {
            Text(
                "Location permission is required.",
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

            Button(onClick = onSignOut) { Text("Sign out") }
            return@Column
        }

        Button(onClick = { fetchLocation(context) { lat, lon, err ->
            error = err
            latLonText = if (err == null) "lat=$lat, lon=$lon" else "No location yet"
        }}) {
            Text("Get Current Location")
        }

        Text(latLonText)

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Text(
            "Later (after PR merge): call WeatherViewModel.load(lat, lon) using these values.",
            style = MaterialTheme.typography.bodySmall
        )

        Button(onClick = onSignOut) { Text("Sign out") }
    }
}

@SuppressLint("MissingPermission")
private fun fetchLocation(
    context: android.content.Context,
    onResult: (lat: Double?, lon: Double?, error: String?) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    fused.lastLocation
        .addOnSuccessListener { loc ->
            if (loc != null) onResult(loc.latitude, loc.longitude, null)
            else onResult(null, null, "Location was null (common on emulator). Try on a physical device or enable emulator location.")
        }
        .addOnFailureListener { e ->
            onResult(null, null, e.message ?: "Failed to get location")
        }
}
