package com.example.saveeats.ui.profile.lastOrders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.Order
import com.example.saveeats.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel : ViewModel() {
    private val repository = ProfileRepository()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _orders.value = repository.getOrders()
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить историю заказов"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
