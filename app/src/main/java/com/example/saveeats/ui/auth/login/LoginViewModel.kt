package com.example.saveeats.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.api.RetrofitClient
import com.example.saveeats.data.models.auth.LoginState
import com.example.saveeats.data.models.auth.UserLoginRequest
import com.example.saveeats.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class LoginViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onLoginClicked(email: String, password: String) {
        // Валидация
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email и пароль не могут быть пустыми")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val request = UserLoginRequest(email, password)
                val response = repository.loginUser(request)
                //RetrofitClient.authInterceptor.accessToken = response.accessToken
                _loginState.value = LoginState.Success(response.accessToken)

            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    401 -> "Неверный email или пароль"
                    500 -> "Ошибка сервера"
                    else -> "Ошибка: ${e.message()}"
                }
                _loginState.value = LoginState.Error(errorMessage)

            } catch (e: IOException) {
                _loginState.value = LoginState.Error("Проблема с подключением к интернету")

            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Неизвестная ошибка: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
