package com.example.saveeats.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.ProfileData
import com.example.saveeats.data.repository.profileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProfileViewModel(private val repository: profileRepository = profileRepository()): ViewModel()
{
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _profileData = MutableStateFlow<ProfileData?>(null)
    val profileData: StateFlow<ProfileData?> = _profileData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile(){
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

        }
        try {
            _profileData.value = repository.getProfileData()}
        catch (e: Exception){
            _error.value = "Ошибка загрузки профиля: ${e.message}"
        }
        finally {
            _isLoading.value = false
        }

    }

    fun onFavoritesClick() { /* TODO */ }
    fun onOrdersClick() { /* TODO */ }
    fun onAddressClick() { /* TODO */ }
    fun onAchievementsClick() { /* TODO */ }

    fun onSettingsClick() { /* TODO */ }


}







