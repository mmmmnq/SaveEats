package com.example.saveeats.data.api

import com.example.saveeats.data.models.auth.UserLoginRequest
import com.example.saveeats.data.models.auth.AuthTokenResponse
import com.example.saveeats.data.models.User
import com.example.saveeats.data.models.auth.UserRegisterRequest
import com.example.saveeats.data.models.auth.UserResponse

import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.GET


interface AuthService {
    @POST("api/auth/login")
    suspend fun login(@Body request: UserLoginRequest): AuthTokenResponse

    @GET("api/auth/me")
    suspend fun getMe(): User

    @POST("api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): UserResponse

}