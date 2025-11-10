package com.example.saveeats.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.Offer
import com.example.saveeats.data.repository.restCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OfferDetailViewModel : ViewModel() {

    private val repository = restCardRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _offer = MutableStateFlow<Offer?>(null)
    val offer: StateFlow<Offer?> = _offer.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadOffer(offerId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val allOffers = repository.getRestCards()
                val foundOffer = allOffers.find { it.id == offerId }

                if (foundOffer != null) {
                    _offer.value = foundOffer
                } else {
                    _error.value = "Товар не найден"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // бронь
    fun bookOffer() {
        viewModelScope.launch {
            // TODO: Логика бронирования
            // Например: repository.bookOffer(_offer.value?.id)
        }
    }
}