package com.example.shale_nammapride.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shale_nammapride.data.model.User
import com.example.shale_nammapride.data.model.UserRole
import com.example.shale_nammapride.data.repository.AuthRepository
import com.example.shale_nammapride.data.repository.AuthRepositoryImpl
import com.example.shale_nammapride.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val authState: StateFlow<UiState<User>> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                if (user != null) {
                    val fullUser = repository.getUserDetails(user.uid)
                    _currentUser.value = fullUser
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            repository.login(email, password)
                .onSuccess { user ->
                    _authState.value = UiState.Success(user)
                    _currentUser.value = user
                }
                .onFailure { error ->
                    _authState.value = UiState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun register(user: User, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            repository.register(user, password)
                .onSuccess { registeredUser ->
                    _authState.value = UiState.Success(registeredUser)
                    _currentUser.value = registeredUser
                }
                .onFailure { error ->
                    _authState.value = UiState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _currentUser.value = null
            _authState.value = UiState.Idle
        }
    }
}
