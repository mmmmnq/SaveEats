package com.example.saveeats.data.api

import com.example.saveeats.data.models.CartRequest
import com.example.saveeats.data.models.CartResponse
import com.example.saveeats.data.models.offers.OfferCreate
import com.example.saveeats.data.models.offers.OfferResponse
import com.example.saveeats.data.models.offers.OfferUpdate
import retrofit2.http.*

/**
 * Сервис для работы с офферами
 */
interface OfferService {
    
    /**
     * Получить все офферы
     * @param businessId опциональный фильтр по ID бизнеса
     * @param onlyAvailable показывать только доступные (по умолчанию true)
     */
    @GET("api/offers")
    suspend fun getOffers(
        @Query("business_id") businessId: Int? = null,
        @Query("only_available") onlyAvailable: Boolean = true
    ): List<OfferResponse>
    
    /**
     * Получить конкретный оффер по ID
     */
    @GET("api/offers/{offer_id}")
    suspend fun getOffer(
        @Path("offer_id") offerId: Int
    ): OfferResponse
    
    /**
     * Получить мои офферы (для бизнес-владельца)
     * Требуется аутентификация с ролью BUSINESS_OWNER
     */
    @GET("api/offers/me/all")
    suspend fun getMyOffers(): List<OfferResponse>
    
    /**
     * Создать новый оффер
     * Требуется аутентификация с ролью BUSINESS_OWNER
     */
    @POST("api/offers")
    suspend fun createOffer(
        @Body offer: OfferCreate
    ): OfferResponse
    
    /**
     * Обновить оффер
     * Требуется аутентификация с ролью BUSINESS_OWNER
     */
    @PUT("api/offers/{offer_id}")
    suspend fun updateOffer(
        @Path("offer_id") offerId: Int,
        @Body offer: OfferUpdate
    ): OfferResponse

    @POST("api/orders")
    suspend fun createOrder(
        @Body request: CartRequest
    ): CartResponse
}

