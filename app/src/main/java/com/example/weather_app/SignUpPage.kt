package com.example.weather_app

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

class SignUpPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    public fun SignUp(view : View?) {
        Toast.makeText(this, "SINGING YOU IN...", Toast.LENGTH_SHORT).show()    // Debug just to show that button works
        // Get values of username and password field
        var username = findViewById<EditText>(R.id.userNameEditText).text
        var password = findViewById<EditText>(R.id.passwordEditText).text
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Username and password files must not be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // Add user to DB
    }
    public fun SignIn(view : View?) {
        startActivity(Intent(this, SignInPage::class.java));
    }
}