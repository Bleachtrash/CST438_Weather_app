package com.example.weather_app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Shapes
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

class SignUpPage : ComponentActivity() {
    var UsernameValue: String = ""
    var PasswordValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column(
                modifier = Modifier.Companion.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    10.dp,
                    Alignment.Companion.CenterVertically
                ),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            ) {
                var UsernameText = remember { mutableStateOf("") }
                var PasswordText = remember { mutableStateOf("") }

                Text("Create an Account!", fontSize = 30.sp)
                Spacer(Modifier.Companion.size(25.dp))

                TextField(
                    value = UsernameText.value,
                    onValueChange = {
                        UsernameText.value = it
                        UsernameValue = UsernameText.value
                    },
                    label = { Text("Username") }
                )
                TextField(
                    value = PasswordText.value,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        PasswordText.value = it
                        PasswordValue = PasswordText.value
                    },
                    label = { Text("Password") }
                )
                TextButton({ signUp() }, "Sign Up")
                Text("Or...?", fontSize = 18.sp)
                TextButton({ signIn() }, "Back to Sign In")
            }
        }
    }
    @Composable
    fun TextButton(onClick: () -> Unit, text: String) {
        Button(
            modifier = Modifier.Companion.size(width = 250.dp, height = 50.dp),
            onClick = { onClick() },
            shape = Shapes(large = RoundedCornerShape(4.dp)).large
        ) {
            Text(text = text, fontSize = 20.sp)
        }
    }
    public fun signUp() {
        Toast.makeText(this, "SINGING YOU IN...", Toast.LENGTH_SHORT).show()    // Debug just to show that button works
        // Get values of username and password field
        if(UsernameValue.isEmpty() || PasswordValue.isEmpty()){
            Toast.makeText(this, "Username and password files must not be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // Add user to DB
    }
    public fun signIn() {
        startActivity(Intent(this, SignInPage::class.java));
    }
}