package com.example.weather_app.ui.auth

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun SignInRoute(
    viewModel: SignInViewModel,
    onSignedIn: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SignInEvent.SignedIn -> onSignedIn()
                is SignInEvent.Message -> snackbarHostState.showSnackbar(event.text)
            }
        }
    }

    SignInScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onSignIn = viewModel::signIn,
        onGoToSignUp = onGoToSignUp
    )
}
