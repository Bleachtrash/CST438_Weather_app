package com.example.weather_app


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.AutoText
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.widget.TextViewCompat
import com.example.weather_app.ui.favorites.FavoritesScreen
import com.example.weather_app.ui.favorites.FavoritesViewModel
import com.google.android.gms.location.*
import java.util.jar.Manifest

class LocationPage : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the latitude and longitude
        var lat = intent.getStringExtra("LAT")
        var lon = intent.getStringExtra("LON")
        if(lat.isNullOrBlank() || lon.isNullOrBlank()){

            // Default to the middle of Monterey
            lat = "36.5972"
            lon = "-121.8971"

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
        /**
         *      Get weather
         *      call api.weather.gov/{lat},{lon} to get place name and forecast url -> do here
         *          Placename = properties/relativeLocation/properties/City
         *          forecast url = properties/forecastHourly
         *      call {forecast url} to get forecast
         *          Current temp = properties/periods/0/temperature -> do here
         *          Current weather = properties/periods/0/shortForecast -> do here
         *          Get next ~10 hours of upcoming weather -> get on fly when creating the cards
         *              Time = properties/periods/i/startTime.subString(11, 15) -> converted to 12 hr
         *              Temp = properties/periods/i/temperature
         */

        /**
         * Create the UI
         *      Location
         *      Current temp & weather(?)
         *      Forecasted temp & weather(?)
         *      "Log out" and "view favorites" buttons
         */

        val Location: String = "Monterey Bay"
        val Temp: String = "100°F"
        val Weather: String = "Cloudy"
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.size(100.dp))
                Text(Location, modifier = Modifier.width(350.dp), fontSize = (70 - 2*Location.length).sp, textAlign = TextAlign.Center)
                Spacer(Modifier.size(50.dp))
                Text(Temp, fontSize = 55.sp)
                Text(Weather, fontSize = 25.sp)
                Spacer(Modifier.size(80.dp))
                val scrollState = rememberScrollState()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .background(
                            color = Color(200, 140, 200, 255),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .width(350.dp)
                        .horizontalScroll(scrollState)
                ) {
                    for (i in 1..10){
                        // Get time and temp from api as described above
                        TimeCard(i.toString()+":00", "10°F")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .background(Color(255, 200, 255, 255))
                        .fillMaxWidth()
                ){
                    Button(onClick = { LogOut() },
                        modifier = Modifier
                            .width(150.dp)
                            .padding(10.dp),
                        shape = RoundedCornerShape(5.dp)) {
                        Text("Log out")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { Favorites() },
                        modifier = Modifier
                            .width(150.dp)
                            .padding(10.dp),
                        shape = RoundedCornerShape(5.dp)) {
                        Text("Favorites")
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

    fun LogOut(){
        startActivity(Intent(this, SignInPage::class.java))
    }
    fun Favorites(){
        if(intent.getStringExtra("IS_GUEST").toString().equals("true")){
            Toast.makeText(this, "Guests cannot access favorites page", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, "Redirecting to favorites page...", Toast.LENGTH_SHORT).show()
        // Start favorites activity
    }

}