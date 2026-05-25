package com.innovagab.app.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innovagab.app.data.auth.AuthRepository
import com.innovagab.app.data.auth.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object CheckingSession : AuthUiState
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class Authenticated(val profile: UserProfile) : AuthUiState
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.CheckingSession)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        if (!repository.isLoggedIn) {
            _uiState.value = AuthUiState.Idle
            return
        }
        viewModelScope.launch {
            repository.fetchCurrentProfile()
                .onSuccess { _uiState.value = AuthUiState.Authenticated(it) }
                .onFailure { _uiState.value = AuthUiState.Idle }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.signIn(email, password)
                .onSuccess { _uiState.value = AuthUiState.Authenticated(it) }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Erro desconhecido") }
        }
    }

    fun signOut() {
        repository.signOut()
        _uiState.value = AuthUiState.Idle
    }

    fun dismissError() {
        _uiState.value = AuthUiState.Idle
    }
}
