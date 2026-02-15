package com.example.saveeats.data.models.auth

import com.google.gson.annotations.SerializedName

data class UserLoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserRegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("role") val role: String
)

data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("role") val role: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)