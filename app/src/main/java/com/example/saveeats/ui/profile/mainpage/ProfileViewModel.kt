package com.example.saveeats.ui.profile.mainpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.User
import com.example.saveeats.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(private val repository: ProfileRepository = ProfileRepository()): ViewModel()
{
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _user = MutableStateFlow<User?>(null) // ← теперь User
    val user: StateFlow<User?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val user = repository.getProfile() // ← получаем User
                _user.value = user
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки профиля: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }



}






