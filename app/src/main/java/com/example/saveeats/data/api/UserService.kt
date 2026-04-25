package com.example.saveeats.data.api

import com.example.saveeats.data.models.OrderResponse
import com.example.saveeats.data.models.User
import com.example.saveeats.data.models.offers.Business
import com.google.gson.annotations.SerializedName
import retrofit2.http.*

interface UserService {
    @GET("api/users/me")
    suspend fun getMe(): User

    @GET("api/users/me/orders")
    suspend fun getMyOrders(): List<OrderResponse>

    @GET("api/users/me/favorites")
    suspend fun getFavorites(): List<Business>

    @POST("api/users/me/favorites/{business_id}")
    suspend fun toggleFavorite(
        @Path("business_id") businessId: Int
    ): FavoriteToggleResponse

    @PUT("api/users/me/avatar")
    suspend fun updateAvatar(@Body request: AvatarUpdateRequest): User
}

data class FavoriteToggleResponse(
    val message: String,
    @SerializedName("is_favorite")
    val isFavorite: Boolean
)

data class AvatarUpdateRequest(
    @SerializedName("avatar_url")
    val avatarUrl: String
)
