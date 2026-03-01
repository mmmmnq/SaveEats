package com.example.saveeats.data.models

import com.google.gson.annotations.SerializedName


data class CartRequest(
    @SerializedName("offer_id")
    val offerid:Int,

    @SerializedName("quantity")
    val quantity:Int
)

data class CartResponse(
    @SerializedName("id")
    val id:Int,
    val status:String,
    @SerializedName("total_price")
    val totalPrice: Double
)