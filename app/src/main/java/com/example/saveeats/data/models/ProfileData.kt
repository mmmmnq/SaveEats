package com.example.saveeats.data.models

data class ProfileData(
    val name: String,
    val email: String,
    val savedBoxes: Int,
    val moneySaved: Int,
    val co2Saved: Int,
    val favoriteCount: Int,
    val ordersCount: Int,
    val address: String
)