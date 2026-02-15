package com.example.saveeats.data.models.offers

import com.google.gson.annotations.SerializedName

/**
 * Запрос на обновление оффера (все поля опциональны)
 */
data class OfferUpdate(
    @SerializedName("business_id")
    val businessId: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = null,
    @SerializedName("original_price")
    val originalPrice: Double? = null,
    @SerializedName("quantity_available")
    val quantityAvailable: Int? = null,
    @SerializedName("pickup_start")
    val pickupStart: String? = null, // ISO 8601 format
    @SerializedName("pickup_end")
    val pickupEnd: String? = null // ISO 8601 format
)

