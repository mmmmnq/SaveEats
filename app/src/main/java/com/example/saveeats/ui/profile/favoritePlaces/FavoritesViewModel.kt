package com.example.saveeats.ui.profile.favoritePlaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.offers.Business
import com.example.saveeats.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: ProfileRepository = ProfileRepository()) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Business>>(emptyList())
    val favorites: StateFlow<List<Business>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _favorites.value = repository.getFavorites()
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить избранные заведения"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(businessId: Int) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(businessId)
                loadFavorites() // Refresh the list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
