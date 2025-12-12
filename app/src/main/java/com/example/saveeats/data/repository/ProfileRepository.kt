package com.example.saveeats.data.repository

import com.example.saveeats.data.api.RetrofitClient
import com.example.saveeats.data.models.User


class ProfileRepository {


    suspend fun getProfile():User
    {
        return RetrofitClient.authService.getMe()

    }

}