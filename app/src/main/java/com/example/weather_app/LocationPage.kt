package com.example.weather_app


import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.jar.Manifest

class LocationPage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the latitude and longitude
        var lat = intent.getStringExtra("LAT")
        var lon = intent.getStringExtra("LON")
        if(lat.isNullOrBlank() || lon.isNullOrBlank()){

            lat = "36.7898"
            lon = "-122.1090"

            // Get the phones location
            var LocationProvider: FusedLocationProviderClient
            LocationProvider = LocationServices.getFusedLocationProviderClient(this)
            val requestPermissionLauncher =
                registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
                    val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

                    if (fineLocationGranted || coarseLocationGranted) {
                        // Permissions granted, get location
                        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                LocationProvider.lastLocation.addOnCompleteListener(this) { task ->
                                    val location: Location? = task.result
                                    if(location != null) {
                                        lat = location.latitude.toString()
                                        lon = location.longitude.toString()
                                    }
                                }
                            }
                        }
                    } else {
                        // Permissions denied, handle accordingly (e.g., show a message)
                        Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            Toast.makeText(this, "Lat: $lat\nLon: $lon", Toast.LENGTH_SHORT).show()
        }
        // Get the weather information from lat and lon
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.size(150.dp))
                Text("[LOCATION]", fontSize = 30.sp)
                Spacer(Modifier.size(50.dp))
                Text("50°F", fontSize = 100.sp)
                Spacer(Modifier.size(50.dp))
                val scrollState = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .background(
                            color = Color.Blue,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(350.dp)
                        .horizontalScroll(scrollState)
                ) {
                    for (i in 1..10){
                        TimeCard(i.toString()+":00", "10°F")
                    }
                }
            }
        }
    }

    @Composable
    fun TimeCard(time: String, temp: String){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(5.dp)
                .background(
                    color = Color.White, shape = RoundedCornerShape(5.dp)
                )
                .width(50.dp)
                .border(1.5.dp, Color.Black, RoundedCornerShape(5.dp))
        ) {
            Text(time, modifier = Modifier.padding(top = 3.dp))
            Text(temp, modifier = Modifier.padding(bottom = 3.dp))
        }
    }

}