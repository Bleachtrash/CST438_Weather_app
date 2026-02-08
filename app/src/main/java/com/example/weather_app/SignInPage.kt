package com.example.weather_app

import android.content.Context
import android.content.Intent
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInPage : ComponentActivity() {
    var UsernameValue: String = ""
    var PasswordValue: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var UsernameText = remember { mutableStateOf("") }
                var PasswordText = remember { mutableStateOf("") }

                Text("WELCOME!", fontSize = 30.sp)
                Spacer(Modifier.size(25.dp))

                TextField(
                    value = UsernameText.value,
                    onValueChange = {
                        UsernameText.value = it
                        UsernameValue = UsernameText.value},
                    label = {Text("Username")}
                )
                TextField(
                    value = PasswordText.value,
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = {
                        PasswordText.value = it
                        PasswordValue = PasswordText.value},
                    label = {Text("Password")}
                )
                TextButton({signIn()}, "Sign In")
                Text("Don't Have an Account?", fontSize = 18.sp)
                TextButton({signUp()}, "Sign Up")
                Text("Or...", fontSize = 18.sp)
                TextButton({Guest()}, "Continue as Guest")
            }
        }

    }
    @Composable
    fun TextButton(onClick: () -> Unit, text: String) {
        Button(modifier = Modifier.size(width = 250.dp, height = 50.dp), onClick = { onClick() }, shape = Shapes(large = RoundedCornerShape(4.dp)).large){
            Text(text = text, fontSize = 20.sp)
        }
    }
    public fun signIn(){
        Toast.makeText(this, "Username: $UsernameValue\nPassword: $PasswordValue", Toast.LENGTH_SHORT).show()    // Debug just to show that button works
        if(UsernameValue.isEmpty() || PasswordValue.isEmpty()){
            Toast.makeText(this, "Username and password files must not be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // Check database to see if user exists and password matches
    }
    public fun signUp(){
        startActivity(Intent(this, SignUpPage::class.java));
    }
    public fun Guest(){
        Toast.makeText(this, "SIGNING YOU IN AS GUEST..", Toast.LENGTH_SHORT).show()    // Debug toast
        // Sign user in using guest profile
            // Possibly just redirect to home page and set some is_guest bool to true or smth
    }
}