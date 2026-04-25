package com.example.saveeats.data.repository

import com.example.saveeats.data.api.RetrofitClient
import com.example.saveeats.data.models.Order
import com.example.saveeats.data.models.User


class ProfileRepository {


    suspend fun getProfile(): User {
        return RetrofitClient.userService.getMe()
    }

    suspend fun getOrders(): List<Order> {
        val response = RetrofitClient.userService.getMyOrders()
        return response.map { res ->
            Order(
                id = res.id,
                date = formatOrderDate(res.createdAt),
                status = res.status,
                totalPrice = res.totalPrice.toInt(),
                businessName = res.offer?.business?.name ?: "Ресторан",
                businessLogoUrl = res.offer?.business?.logo_url,
                offerName = res.offer?.title ?: "Сюрприз-бокс",
                quantity = res.quantity,
                pickupCode = res.pickupCode
            )
        }
    }

    suspend fun completeOrder(orderId: Int): Boolean {
        return try {
            RetrofitClient.offerService.completeOrder(orderId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getFavorites(): List<com.example.saveeats.data.models.offers.Business> {
        return RetrofitClient.userService.getFavorites()
    }

    suspend fun toggleFavorite(businessId: Int): Boolean {
        val response = RetrofitClient.userService.toggleFavorite(businessId)
        return response.isFavorite
    }

    private fun formatOrderDate(isoDate: String): String {
        return try {
            // "2024-04-03T12:30:00" -> "03.04.2024"
            val datePart = isoDate.split("T")[0]
            val parts = datePart.split("-")
            "${parts[2]}.${parts[1]}.${parts[0]}"
        } catch (e: Exception) {
            isoDate
        }
    }

}