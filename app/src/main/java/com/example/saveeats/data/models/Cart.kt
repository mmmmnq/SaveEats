package com.example.saveeats.data.models

// Товар в корзине (на основе Offer, но с количеством и итоговой ценой)
data class CartItem(
    val offerId: Int,            // ID товара (из Offer.id)
    val offerName: String,       // (из Offer.name)
    val category: String,        // (из Offer.category)
    val originalPrice: Int,      // (из Offer.oldPrice)
    val discountedPrice: Int,    // (из Offer.newPrice)
    val discount: Int,           // (из Offer.discount)
    val quantity: Int = 1,       // количество (новое поле)
    val distance: Double = 0.0,  // (из Offer.distance, можно сохранить как snapshot)
    val pickupTime: String = ""
     // (из Offer.time)
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