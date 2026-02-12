package com.example.weather_app.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.weather_app.data.SessionManager
import com.example.weather_app.data.UserRepository
import kotlinx.coroutines.launch
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun SignInRoute(
    onSignedIn: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { UserRepository.getInstance(context) }

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign In", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)

        }

        Button(
            onClick = {
                error = null
                if (username.isBlank() || password.isBlank()) {
                    error = "Enter username and password"
                    return@Button
                }
                loading = true
                scope.launch {
                    try {
                        val hashed = hashPassword(password)
                        val user = repo.login(username, hashed)
                        if (user != null) {
                            SessionManager.setUser(user.id)
                            Toast.makeText(context, "Signed in", Toast.LENGTH_SHORT).show()
                            onSignedIn()
                        } else {
                            error = "Invalid username or password"
                        }
                    } catch (e: Exception) {
                        error = e.message ?: "Sign in Failed"
                    } finally {
                        loading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Signing in..." else "Sign In")
        }

        Button(
            onClick = onGoToSignUp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create an account")
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpRoute(
    onSignedUp: () -> Unit,
    onGoToSignIn: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember {UserRepository.getInstance(context) }

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConfirm by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = { passwordConfirm = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                error = null
                if (username.isBlank() || password.isBlank()) {
                    error = "Enter username and password"
                    return@Button
                }
                if (password != passwordConfirm) {
                    error = "Passwords do not match"
                    return@Button
                }
                loading = true
                scope.launch {
                    try {
                        val hashed = hashPassword(password)
                        val insertedId = repo.register(username.trim(), hashed)
                        if (insertedId == -1L) {
                            error = "Username already exists"
                        } else {
                            SessionManager.setUser(insertedId)
                            Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
                            onSignedUp()
                        }
                    } catch (e: Exception) {
                        error = e.message ?: "Sign up failed"
                    } finally {
                        loading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Creating..." else "Create account")
        }

        Button(
            onClick = onGoToSignIn,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Sign In")
        }
    }
}

private fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(password.toByteArray(Charsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
}
