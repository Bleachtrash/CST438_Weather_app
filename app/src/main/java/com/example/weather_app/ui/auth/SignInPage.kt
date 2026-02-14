package com.example.weather_app.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_app.ui.auth.SignUpPage
import com.example.weather_app.ui.weather.LocationPage
import com.google.android.gms.location.LocationServices

class SignInPage : ComponentActivity() {

    var UsernameValue: String = ""
    var PasswordValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLocationPermission()

        setContent {
            Column(
                modifier = Modifier.Companion.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.Companion.CenterVertically
                ),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                val UsernameText = remember { mutableStateOf("") }
                val PasswordText = remember { mutableStateOf("") }

                Text("WELCOME!", fontSize = 30.sp)
                Spacer(Modifier.Companion.size(25.dp))

                TextField(
                    value = UsernameText.value,
                    onValueChange = {
                        UsernameText.value = it
                        UsernameValue = it
                    },
                    label = { Text("Username") }
                )

                TextField(
                    value = PasswordText.value,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        PasswordText.value = it
                        PasswordValue = it
                    },
                    label = { Text("Password") }
                )

                TextButton({ signIn() }, "Sign In")
                Text("Don't Have an Account?", fontSize = 18.sp)
                TextButton({ signUp() }, "Sign Up")
                Text("Or...", fontSize = 18.sp)
                TextButton({ guest() }, "Continue as Guest")
            }
        }
    }

    @Composable
    fun TextButton(onClick: () -> Unit, text: String) {
        Button(
            modifier = Modifier.Companion.size(width = 250.dp, height = 50.dp),
            onClick = onClick
        ) {
            Text(text = text, fontSize = 20.sp)
        }
    }

    fun signIn() {
        Toast.makeText(
            this,
            "Username: $UsernameValue\nPassword: $PasswordValue",
            Toast.LENGTH_SHORT
        ).show()

        if (UsernameValue.isEmpty() || PasswordValue.isEmpty()) {
            Toast.makeText(
                this,
                "Username and password fields must not be blank",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
    }

    fun signUp() {
        startActivity(Intent(this, SignUpPage::class.java))
    }

    fun guest() {
        startLocationActivity("true")
    }

    fun requestLocationPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun startLocationActivity(isGuest: String) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    startActivity(Intent(this, LocationPage::class.java).apply {
                        putExtra("LAT", location.latitude.toString())
                        putExtra("LON", location.longitude.toString())
                        putExtra("IS_GUEST", isGuest)
                    })
                } else {
                    startActivity(Intent(this, LocationPage::class.java).apply {
                        putExtra("LAT", "36.5975")
                        putExtra("LON", "-121.899")
                        putExtra("IS_GUEST", isGuest)
                    })
                }
            }
            .addOnFailureListener {
                startActivity(Intent(this, LocationPage::class.java).apply {
                    putExtra("LAT", "36.5975")
                    putExtra("LON", "-121.899")
                    putExtra("IS_GUEST", isGuest)
                })
            }
    }
}