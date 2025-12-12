package com.example.saveeats.data.api

import com.example.saveeats.SaveEatsApplication
import com.example.saveeats.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.saveeats.data.models.offers.BusinessService
object RetrofitClient {

    private const val BASE_URL = "http://192.168.0.196:8000/"

    private val tokenManager by lazy {
        TokenManager(SaveEatsApplication.instance)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val authInterceptor = AuthInterceptor(tokenManager)

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)  // ← Добавили AuthInterceptor
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val businessService: BusinessService by lazy {
        retrofit.create(BusinessService::class.java)
    }
    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}