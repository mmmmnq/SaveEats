package com.example.saveeats.data.models.offers

import com.google.gson.annotations.SerializedName

data class OfferResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val price: Double,

    @SerializedName("original_price")
    val originalPrice: Double?,

    @SerializedName("quantity_available")
    val quantity: Int,

    @SerializedName("pickup_start")
    val pickupStart: String,

    @SerializedName("pickup_end")
    val pickupEnd: String,


    @SerializedName("image_url")
    val imageUrl: String? = null,

    val businessId: Int, // Это у нас было

    // 👇 ВАЖНО: Вложенный объект (Бэкенд теперь его шлет благодаря joinedload)
    val business: Business? = null
)