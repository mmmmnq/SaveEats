package com.example.saveeats.data.models.offers

import com.google.gson.annotations.SerializedName
import retrofit2.http.Query
import retrofit2.http.GET


data class Business(
    val id: Int,
    val owner_id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val logo_url:String?,
    @SerializedName("cover_image_url")
    val cover_image_url: String?,
    val rating: Double?,
    val distance_km: Double,

)

interface BusinessService {
    @GET("api/businesses/nearby")
    suspend fun getNearby(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius_km") radiusKm: Double = 10.0 // Дефолтный радиус
    ): List<Business>
}