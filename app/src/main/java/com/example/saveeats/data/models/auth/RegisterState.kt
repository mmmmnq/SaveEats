package com.example.saveeats.data.models.auth

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: UserResponse, val token: String?) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

