package com.example.saveeats.ui.auth.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.auth.RegisterState
import com.example.saveeats.data.models.auth.UserLoginRequest
import com.example.saveeats.data.models.auth.UserRegisterRequest
import com.example.saveeats.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class RegisterViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun onRegisterClicked(email: String, password: String, fullName: String?) {
        // Валидация
        if (email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("Email и пароль не могут быть пустыми")
            return
        }

        if (password.length < 6) {
            _registerState.value = RegisterState.Error("Пароль должен содержать минимум 6 символов")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerState.value = RegisterState.Error("Введите корректный email адрес")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val roleForRegistration = "CUSTOMER"

                val request = UserRegisterRequest(
                    email = email,
                    password = password,
                    fullName = fullName,
                    role = roleForRegistration
                )

                val newUser = repository.registerUser(request)

                // После успешной регистрации автоматически входим
                try {
                    val loginRequest = UserLoginRequest(email, password)
                    val loginResponse = repository.loginUser(loginRequest)
                    _registerState.value = RegisterState.Success(newUser, loginResponse.accessToken)
                } catch (loginError: Exception) {
                    // Если автоматический вход не удался, все равно показываем успех регистрации
                    _registerState.value = RegisterState.Success(newUser, null)
                }

            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    400 -> "Некорректные данные. Проверьте введенную информацию"
                    409 -> "Пользователь с таким email уже существует"
                    500 -> "Ошибка сервера"
                    else -> "Ошибка: ${e.message()}"
                }
                _registerState.value = RegisterState.Error(errorMessage)

            } catch (e: IOException) {
                _registerState.value = RegisterState.Error("Проблема с подключением к интернету")

            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Неизвестная ошибка: ${e.message}")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}