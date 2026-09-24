package com.example.crisisdeskmobile.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crisisdeskmobile.data.model.User
import com.example.crisisdeskmobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Form fields state
    var email = MutableStateFlow("")
    var password = MutableStateFlow("")
    var displayName = MutableStateFlow("")

    fun login() {
        val emailStr = email.value.trim()
        val passwordStr = password.value.trim()

        if (emailStr.isEmpty() || passwordStr.isEmpty()) {
            _uiState.value = AuthUiState.Error("Please enter both email and password")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(emailStr, passwordStr)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success(result.getOrNull()!!)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Authentication failed"
                _uiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun register() {
        val emailStr = email.value.trim()
        val passwordStr = password.value.trim()
        val nameStr = displayName.value.trim()

        if (emailStr.isEmpty() || passwordStr.isEmpty() || nameStr.isEmpty()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields")
            return
        }

        if (passwordStr.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(emailStr, passwordStr, nameStr)
            if (result.isSuccess) {
                _uiState.value = AuthUiState.Success(result.getOrNull()!!)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Registration failed"
                _uiState.value = AuthUiState.Error(errorMsg)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
