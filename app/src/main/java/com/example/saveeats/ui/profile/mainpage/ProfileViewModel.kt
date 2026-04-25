package com.example.saveeats.ui.profile.mainpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.User
import com.example.saveeats.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async


import com.example.saveeats.data.models.Order
import com.example.saveeats.data.models.offers.Business
import kotlinx.coroutines.flow.combine

class ProfileViewModel(private val repository: ProfileRepository = ProfileRepository()): ViewModel()
{
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _favorites = MutableStateFlow<List<Business>>(emptyList())
    val favorites: StateFlow<List<Business>> = _favorites.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Состояние для статистики (расчет на основе списка заказов)
    val stats = _orders.combine(_favorites) { ordersList, favs ->
        val completedOrders = ordersList.filter { order ->
            val s = order.status.uppercase()
            s == "COMPLETED" || s == "CONFIRMED" || s == "RESERVED"
        }
        val boxesCount = completedOrders.sumOf { it.quantity.toInt() }
        val moneySaved = completedOrders.sumOf { it.totalPrice.toInt() }
        val co2Saved = (boxesCount * 2.5).toInt()
        
        ProfileStats(
            savedBoxes = boxesCount,
            moneySaved = moneySaved,
            co2Saved = co2Saved
        )
    }

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Используем async для параллельного выполнения
                val userJob = async { repository.getProfile() }
                val ordersJob = async { repository.getOrders() }
                val favoritesJob = async { repository.getFavorites() }

                _user.value = userJob.await()
                _orders.value = ordersJob.await()
                _favorites.value = favoritesJob.await()
                
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки данных: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class ProfileStats(
    val savedBoxes: Int,
    val moneySaved: Int,
    val co2Saved: Int
)






