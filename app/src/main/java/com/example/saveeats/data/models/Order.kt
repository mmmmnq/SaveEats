package com.example.saveeats.data.models

import com.example.saveeats.data.models.offers.Business
import com.google.gson.annotations.SerializedName

data class OrderResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("offer_id")
    val offerId: Int,
    val quantity: Int,
    @SerializedName("total_price")
    val totalPrice: Double,
    val status: String,
    @SerializedName("pickup_code")
    val pickupCode: String?,
    @SerializedName("created_at")
    val createdAt: String,
    val offer: OfferInOrderResponse? = null
)

data class OrderItemResponse(
    val id: Int,
    @SerializedName("offer_id")
    val offerId: Int,
    val quantity: Int,
    val price: Double,
    val offer: OfferInOrderResponse? = null
)

data class OfferInOrderResponse(
    val id: Int,
    val title: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    val business: Business?
)

data class Order(
    val id: Int,
    val date: String,
    val status: String,
    val totalPrice: Int,
    val businessName: String,
    val businessLogoUrl: String?,
    val offerName: String,
    val quantity: Int,
    val pickupCode: String? = null
)
