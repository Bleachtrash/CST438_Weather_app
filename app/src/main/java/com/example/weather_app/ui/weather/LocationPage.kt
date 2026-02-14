package com.example.weather_app.ui.weather

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather_app.ui.auth.SignInPage
import com.example.weather_app.model.ForecastPeriod

class LocationPage : ComponentActivity() {

    var lat: String? = null
    var lon: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Get the latitude and longitude
        lat = intent.getStringExtra("LAT")
        lon = intent.getStringExtra("LON")

        setContent {
            val vm: WeatherViewModel = viewModel()
            vm.load(lat.toString().toDouble(), lon.toString().toDouble())
            val uiState = vm.ui.collectAsState().value
            when {
                uiState.isLoading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Companion.CenterHorizontally,
                        modifier = Modifier.Companion.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {

                }
                else -> {
                    SetLayout(uiState.periods, uiState.city)
                }
            }
        }
    }
    @Composable
    fun SetLayout(periods:  List<ForecastPeriod>, city: String?){
        val Location = city.toString()
        val Temperature = periods[0].temperature.toString()
        val Weather = periods[0].shortForecast
        Column(
            modifier = Modifier.Companion.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Spacer(Modifier.Companion.size(100.dp))
            Text(
                Location,
                modifier = Modifier.Companion.width(350.dp),
                fontSize = (70 - 2 * Location.length).sp,
                textAlign = TextAlign.Companion.Center
            )
            Spacer(Modifier.Companion.size(50.dp))
            Text("$Temperature°F", fontSize = 55.sp)
            Text(Weather, fontSize = 25.sp)
            Spacer(Modifier.Companion.size(80.dp))
            val scrollState = rememberScrollState()
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.Companion
                    .background(
                        color = Color(200, 140, 200, 255),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .width(350.dp)
                    .horizontalScroll(scrollState)
            ) {
                for (i in 1..10) {
                    TimeCard(
                        periods[i].startTime.substring(11, 16),
                        periods[i].temperature.toString()
                    )
                }
            }
            Spacer(modifier = Modifier.Companion.weight(1f))
            Row(
                modifier = Modifier.Companion
                    .background(Color(255, 200, 255, 255))
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { LogOut() },
                    modifier = Modifier.Companion
                        .width(150.dp)
                        .padding(10.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                ) {
                    Text("Log out")
                }
                Spacer(Modifier.Companion.weight(1f))
                Button(
                    onClick = { Favorites() },
                    modifier = Modifier.Companion
                        .width(150.dp)
                        .padding(10.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                ) {
                    Text("Favorites")
                }
            }
        }
    }
    @Composable
    fun TimeCard(time: String, temp: String){
        Column(
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            modifier = Modifier.Companion
                .padding(5.dp)
                .background(
                    color = Color.Companion.White,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                )
                .width(50.dp)
                .border(
                    1.5.dp,
                    Color.Companion.Black,
                    androidx.compose.foundation.shape.RoundedCornerShape(5.dp)
                )
        ) {
            Text(time, modifier = Modifier.Companion.padding(top = 3.dp))
            Text("$temp°F", modifier = Modifier.Companion.padding(bottom = 3.dp))
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