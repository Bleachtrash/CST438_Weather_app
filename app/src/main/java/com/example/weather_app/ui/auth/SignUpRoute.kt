package com.example.weather_app.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.weather_app.data.SessionManager
import com.example.weather_app.data.UserRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpRoute(
    onSignedUp: () -> Unit,
    onGoToSignIn: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { UserRepository.getInstance(context) }
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }

    var userError by remember { mutableStateOf<String?>(null) }
    var passError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    var passVisible by rememberSaveable { mutableStateOf(false) }
    var confirmVisible by rememberSaveable { mutableStateOf(false) }

    fun validate(): Boolean {
        val u = username.trim()
        val p = password
        val c = confirm

        userError = if (u.isBlank()) "Enter a username" else null
        passError = when {
            p.isBlank() -> "Enter a password"
            p.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        confirmError = when {
            c.isBlank() -> "Confirm your password"
            p != c -> "Passwords do not match"
            else -> null
        }

        return userError == null && passError == null && confirmError == null
    }

    fun submit() {
        keyboardController?.hide()
        focusManager.clearFocus()

        if (!validate()) return

        val u = username.trim()
        val p = password

        loading = true
        scope.launch {
            try {
                val hashed = PasswordHasher.sha256(p)
                val insertedId = repo.register(u, hashed)

                if (insertedId == -1L) {
                    snackbarHostState.showSnackbar("Username already exists")
                } else {
                    SessionManager.setUser(insertedId)
                    onSignedUp()
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(e.message ?: "Sign up failed")
            } finally {
                loading = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create account") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Let’s get you set up", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Create an account to manage favorites.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; userError = null },
                        label = { Text("Username") },
                        singleLine = true,
                        enabled = !loading,
                        isError = userError != null,
                        supportingText = { Text(userError ?: " ") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passError = null },
                        label = { Text("Password") },
                        singleLine = true,
                        enabled = !loading,
                        isError = passError != null,
                        supportingText = { Text(passError ?: " ") },
                        visualTransformation =
                            if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passVisible = !passVisible }, enabled = !loading) {
                                Icon(
                                    imageVector = if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = confirm,
                        onValueChange = { confirm = it; confirmError = null },
                        label = { Text("Confirm password") },
                        singleLine = true,
                        enabled = !loading,
                        isError = confirmError != null,
                        supportingText = { Text(confirmError ?: " ") },
                        visualTransformation =
                            if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmVisible = !confirmVisible }, enabled = !loading) {
                                Icon(
                                    imageVector = if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (confirmVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { submit() },
                        enabled = !loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (loading) {
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("Creating…")
                        } else {
                            Text("Create account")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Already have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        TextButton(
                            onClick = onGoToSignIn,
                            enabled = !loading,
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Sign in")
                        }
                    }
                }
            }
        }
    }
}
