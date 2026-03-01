package com.example.saveeats.ui.cart

import CartRepository
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.CartItem
import com.example.saveeats.data.models.Offer

import com.example.saveeats.data.models.CartSummary
import com.example.saveeats.data.repository.RestCardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel :ViewModel() {

    private val localCartRepository = CartRepository

    private val networkRepository = RestCardRepository()
    val cartItems: StateFlow<List<CartItem>> = localCartRepository.cartItems

    val cartSummary: StateFlow<CartSummary?> = localCartRepository.cartItems.map { localCartRepository.getSummary() }.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()




    fun removeItem(offerId: Int)
    {
        localCartRepository.removeFromCart(offerId)

    }
    fun clearCart()
    {

            localCartRepository.clearCart()
    }

    fun confirmOrder() {
        val currentItems = cartItems.value


        if (currentItems.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {

                val results = currentItems.map { item ->
                    async {
                        networkRepository.createOrder(
                            offerId = item.offerId,
                            quantity = item.quantity
                        )
                    }
                }.awaitAll()


                val allSuccess = results.all { it }

                if (allSuccess) {
                    clearCart()
                    _uiEvent.emit("Заказ успешно оформлен! 🎉")
                } else {
                    _uiEvent.emit("Часть товаров не удалось забронировать")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("Ошибка соединения: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
