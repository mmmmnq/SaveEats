package com.example.saveeats.ui.cart

import com.example.saveeats.data.repository.CartRepository
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

enum class PaymentState {
    IDLE, PROCESSING, BANK_RESPONSE, SUCCESS, ERROR
}

class CartViewModel :ViewModel() {

    private val localCartRepository = CartRepository

    private val networkRepository = RestCardRepository()
    val cartItems: StateFlow<List<CartItem>> = localCartRepository.cartItems

    val cartSummary: StateFlow<CartSummary?> = localCartRepository.cartItems.map { localCartRepository.getSummary() }.stateIn(viewModelScope, SharingStarted.Eagerly, null)


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _paymentState = MutableStateFlow(PaymentState.IDLE)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()


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

    fun updatePickupTime(offerId: Int, time: String) {
        localCartRepository.updatePickupTime(offerId, time)
    }

    fun dismissPayment() {
        _paymentState.value = PaymentState.IDLE
    }

    fun confirmOrder() {
        val currentItems = cartItems.value


        if (currentItems.isEmpty()) return

        // Проверка: выбрано ли время для всех товаров
        if (currentItems.any { it.selectedPickupTime == null }) {
            viewModelScope.launch {
                _uiEvent.emit("Пожалуйста, выберите время получения для всех товаров")
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _paymentState.value = PaymentState.PROCESSING
            
            try {
                // 1. Имитируем ожидание банка
                kotlinx.coroutines.delay(2000)
                _paymentState.value = PaymentState.BANK_RESPONSE
                
                // 2. Имитируем получение ответа
                kotlinx.coroutines.delay(1500)

                // 3. Реальный запрос к API
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
                    _paymentState.value = PaymentState.SUCCESS
                    // Даем пользователю увидеть успех перед очисткой
                    kotlinx.coroutines.delay(2000)
                    clearCart()
                    _uiEvent.emit("Заказ успешно оформлен! 🎉")
                } else {
                    _paymentState.value = PaymentState.ERROR
                    _uiEvent.emit("Часть товаров не удалось забронировать")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _paymentState.value = PaymentState.ERROR
                _uiEvent.emit("Ошибка соединения: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
