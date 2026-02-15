package com.example.saveeats.data.api

import android.util.Log
import com.example.saveeats.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = tokenManager.getToken()

        if (token == null) {
            Log.d(TAG, "⚠️ Токен отсутствует")
            return chain.proceed(originalRequest)
        }

        // ДОБАВЬТЕ ЭТУ СТРОКУ для отладки:
        Log.d(TAG, "🔑 Полный токен: $token")

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        Log.d(TAG, "✅ Добавлен Authorization header для ${originalRequest.url}")

        return chain.proceed(newRequest)
    }
}
