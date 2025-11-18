package com.example.saveeats.ui.cart

import CartRepository
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveeats.data.models.CartItem
import com.example.saveeats.data.models.Offer

import com.example.saveeats.data.models.CartSummary

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel :ViewModel() {

    private val cartRepository = CartRepository
    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems

    val cartSummary: StateFlow<CartSummary?> = cartRepository.cartItems.map { cartRepository.getSummary() }.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()




    fun removeItem(offerId: Int)
    {
        cartRepository.removeFromCart(offerId)

    }

    fun clearCart()
    {

            cartRepository.clearCart()

    }

    fun confirmOrder()
    {
        viewModelScope.launch {
            _isLoading.value = true
            // TODO тут отправка на сервер
            cartRepository.clearCart()
            _isLoading.value = false
        }
    }
}