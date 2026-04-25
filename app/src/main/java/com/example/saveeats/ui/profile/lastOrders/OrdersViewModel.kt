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

    private val _activeOrders = MutableStateFlow<List<Order>>(emptyList())
    val activeOrders: StateFlow<List<Order>> = _activeOrders.asStateFlow()

    private val _historyOrders = MutableStateFlow<List<Order>>(emptyList())
    val historyOrders: StateFlow<List<Order>> = _historyOrders.asStateFlow()

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
                val allOrders = repository.getOrders()
                _orders.value = allOrders
                
                // Разделяем заказы на активные и историю
                _activeOrders.value = allOrders.filter { 
                    it.status.uppercase() == "RESERVED" || it.status.uppercase() == "CONFIRMED" 
                }
                _historyOrders.value = allOrders.filter { 
                    it.status.uppercase() != "RESERVED" && it.status.uppercase() != "CONFIRMED" 
                }
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить историю заказов"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeOrder(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.completeOrder(orderId)
            if (success) {
                loadOrders() // Перезагружаем список после успешного завершения
            } else {
                _error.value = "Не удалось подтвердить получение заказа"
            }
            _isLoading.value = false
        }
    }
}
