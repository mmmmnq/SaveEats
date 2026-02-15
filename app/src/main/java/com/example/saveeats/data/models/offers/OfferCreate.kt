package com.example.saveeats.data.models.offers

import com.google.gson.annotations.SerializedName

/**
 * Запрос на создание оффера
 */
data class OfferCreate(
    @SerializedName("business_id")
    val businessId: Int,
    val title: String,
    val description: String?,
    val price: Double,
    @SerializedName("original_price")
    val originalPrice: Double,
    @SerializedName("quantity_available")
    val quantityAvailable: Int,
    @SerializedName("pickup_start")
    val pickupStart: String, // ISO 8601 format
    @SerializedName("pickup_end")
    val pickupEnd: String // ISO 8601 format
)

