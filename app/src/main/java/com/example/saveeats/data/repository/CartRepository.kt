import com.example.saveeats.data.models.CartItem
import com.example.saveeats.data.models.CartSummary
import com.example.saveeats.data.models.Offer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> get() = _cartItems

    fun addToCart(offer: Offer, quantity: Int = 1) {
        val existingItem = _cartItems.value.find { it.offerId == offer.id }
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            _cartItems.value = _cartItems.value.map { if (it.offerId == offer.id) updatedItem else it }
        } else {
            val cartItem = CartItem(
                offerId = offer.id,
                offerName = offer.name,
                category = offer.category,
                originalPrice = offer.oldPrice,
                discountedPrice = offer.newPrice,
                discount = offer.discount,
                quantity = quantity,
                distance = offer.distance.toDouble(),
                pickupTime = offer.time

            )
            _cartItems.value = _cartItems.value + cartItem
        }
    }

    fun removeFromCart(offerId: Int) {
        _cartItems.value = _cartItems.value.filter { it.offerId != offerId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun isInCart(offerId: Int): Boolean =
        _cartItems.value.any { it.offerId == offerId }

    fun getSummary(): CartSummary? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val subtotal = items.sumOf { it.originalPrice * it.quantity }
        val total = items.sumOf { it.discountedPrice * it.quantity }
        val discount = subtotal - total

        return CartSummary(
            items = items,
            subtotal = subtotal,
            total = total,
            discount = discount
        )
    }
}
