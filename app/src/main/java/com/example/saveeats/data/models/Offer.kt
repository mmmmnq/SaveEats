package com.example.saveeats.data.models

import com.example.saveeats.data.models.offers.Business // Импортируем твой класс Business

data class Offer(
    val id: Int,
    val name: String,         // Название пакета (Magic Bag)
    val category: String,     // Выпечка
    val time: String,         // 20:00 - 21:00
    val boxesLeft: Int,
    val oldPrice: Int,
    val newPrice: Int,
    val discount: Int,
    val isAlmostGone: Boolean,
    val imageUrl: String?,

    // 👇 ГЛАВНОЕ ИЗМЕНЕНИЕ: Вкладываем сюда объект Business
    val business: Business
)