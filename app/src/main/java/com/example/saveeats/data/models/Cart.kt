package com.example.saveeats.data.models

// Товар в корзине (с количеством и итоговой ценой)
data class CartItem(
    val offerId: Int,
    val offerName: String,
    val businessName: String,
    val businessLat: Double,
    val businessLon: Double,
    val category: String,
    val originalPrice: Int,
    val discountedPrice: Int,
    val discount: Int,
    val quantity: Int = 1,
    val distance: Double = 0.0,
    val pickupTimeRange: String = "", // Диапазон времени (например "15:00 - 18:00")
    val selectedPickupTime: String? = null // Конкретное время, выбранное пользователем
)

// Итоги корзины
data class CartSummary(
    val items: List<CartItem>,   // ❗️теперь список CartItem, а не Offer
    val subtotal: Int,           // общая цена без скидки
    val total: Int,              // итог к оплате
    val discount: Int            // общая скидка
) {
    val itemCount: Int
        get() = items.sumOf { it.quantity }  // ❗️суммируем количество

    val savings: Int
        get() = discount
}