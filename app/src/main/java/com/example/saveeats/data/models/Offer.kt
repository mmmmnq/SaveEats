package com.example.saveeats.data.models

data class Offer(
    val id: Int,
    val name: String,
    val category: String,
    val distance: Float,
    val time: String,
    val boxesLeft: Int,
    val oldPrice: Int,
    val newPrice: Int,
    val discount: Int,
    val rating : Double,
    val isAlmostGone: Boolean
)