package com.example.weather_app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.SessionManager
import com.example.weather_app.data.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState

    private val _events = MutableSharedFlow<SignInEvent>()
    val events: SharedFlow<SignInEvent> = _events

    fun onUsernameChange(v: String) {
        _uiState.update { it.copy(username = v, usernameError = null) }
    }

    fun onPasswordChange(v: String) {
        _uiState.update { it.copy(password = v, passwordError = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun signIn() {
        val s = _uiState.value
        val u = s.username.trim()
        val p = s.password

        val userErr = if (u.isBlank()) "Enter your username" else null
        val passErr = if (p.isBlank()) "Enter your password" else null

        if (userErr != null || passErr != null) {
            _uiState.update { it.copy(usernameError = userErr, passwordError = passErr) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val hashed = PasswordHasher.sha256(p)
                val user = repo.login(u, hashed)

                if (user != null) {
                    SessionManager.setUser(user.id)
                    _events.emit(SignInEvent.SignedIn)
                } else {
                    _events.emit(SignInEvent.Message("Invalid username or password"))
                }
            } catch (e: Exception) {
                _events.emit(SignInEvent.Message(e.message ?: "Sign in failed"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
