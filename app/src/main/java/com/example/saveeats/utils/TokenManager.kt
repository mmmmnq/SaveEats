package com.example.saveeats.utils


import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "TokenManager"
    }

    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
        Log.d(TAG, "✅ Токен сохранен: ${token.take(20)}...")
    }

    fun getToken(): String? {
        val token = prefs.getString("access_token", null)
        Log.d(TAG, "📖 Получение токена: ${token?.take(20) ?: "NULL - токен не найден!"}")
        return token
    }

    fun clearToken() {
        prefs.edit().remove("access_token").apply()
        Log.d(TAG, "🗑️ Токен удален")
    }

    fun isLoggedIn(): Boolean {
        val token = getToken()
        val loggedIn = token != null
        Log.d(TAG, "🔍 Проверка авторизации: isLoggedIn = $loggedIn")
        return loggedIn
    }
}