package com.example.saveeats.data.repository

import com.example.saveeats.data.api.RetrofitClient
import com.example.saveeats.data.models.auth.AuthTokenResponse
import com.example.saveeats.data.models.auth.UserLoginRequest
import com.example.saveeats.data.models.auth.UserRegisterRequest
import com.example.saveeats.data.models.auth.UserResponse


class AuthRepository
{
    private val authService = RetrofitClient.authService

    suspend fun loginUser(request: UserLoginRequest): AuthTokenResponse {
        return authService.login(request)
    }

    suspend fun registerUser(request: UserRegisterRequest): UserResponse {
        return authService.register(request)
    }

}

