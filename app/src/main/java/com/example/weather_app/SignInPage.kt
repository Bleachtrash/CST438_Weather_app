package com.example.weather_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    public fun signIn(view: View?){
        Toast.makeText(this, "SINGING YOU IN...", Toast.LENGTH_SHORT).show()    // Debug just to show that button works
        // Get values of username and password field
        var username = findViewById<EditText>(R.id.userNameEditText).text
        var password = findViewById<EditText>(R.id.passwordEditText).text
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Username and password files must not be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // Check database to see if user exists and password matches
    }
    public fun signUp(view : View?){
        Toast.makeText(this, "REDIRECTING TO SIGN UP PAGE...", Toast.LENGTH_SHORT).show()   // Debug notif
        // Start SignUpPage activity
    }
    public fun Guest(view : View?){
        Toast.makeText(this, "SIGNING YOU IN AS GUEST..", Toast.LENGTH_SHORT).show()    // Debug toast
        // Sign user in using guest profile
            // Possibly just redirect to home page and set some is_guest bool to true or smth
    }
}