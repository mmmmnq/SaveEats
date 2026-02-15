package com.example.saveeats.data.repository

import com.example.saveeats.data.models.Offer
import com.example.saveeats.data.models.offers.Business
import com.example.saveeats.data.models.offers.OfferResponse

object OfferMapper {

    fun toOffer(offerResponse: OfferResponse, business: Business? = null): Offer {

        // 1. ПОЛУЧЕНИЕ БИЗНЕСА
        // Берем из ответа сервера (вложенный) ИЛИ из аргумента функции
        // Если и там и там пусто — создаем заглушку (чтобы не было краша)
        val safeBusiness = offerResponse.business ?: business ?: Business(
            id = 0,
            owner_id = 0,
            name = "Загрузка...",
            address = "",
            latitude = 0.0,
            longitude = 0.0,
            logo_url = null,
            cover_image_url = null,
            rating = 0.0,
            distance_km = 0.0
        )

        // 2. ВРЕМЯ (Используем твой метод парсинга)
        val startTime = extractTimeFromISO(offerResponse.pickupStart) ?: "00:00"
        val endTime = extractTimeFromISO(offerResponse.pickupEnd) ?: "23:59"
        val timeString = "$startTime - $endTime"

        // 3. ЦЕНА И СКИДКА (Исправление ошибки Double?)
        // Если originalPrice == null, считаем его равным 0.0
        val safeOriginalPrice = offerResponse.originalPrice ?: 0.0

        val discount = if (safeOriginalPrice > 0) {
            ((safeOriginalPrice - offerResponse.price) / safeOriginalPrice * 100).toInt()
        } else {
            0
        }

        // 4. КОЛИЧЕСТВО (Исправление ошибки Unresolved reference)
        // В OfferResponse поле называется quantity
        val isAlmostGone = offerResponse.quantity <= 3

        // 5. КАРТИНКА (Логика приоритета)
        // Если есть картинка у Оффера -> берем её.
        // Если нет -> берем картинку Ресторана.
        val finalImageUrl = offerResponse.imageUrl ?: safeBusiness.cover_image_url

        return Offer(
            id = offerResponse.id,
            name = offerResponse.title,
            category = "Еда",
            time = timeString,
            boxesLeft = offerResponse.quantity, // 👈 Используем .quantity
            oldPrice = safeOriginalPrice.toInt(), // 👈 Используем безопасную переменную
            newPrice = offerResponse.price.toInt(),
            discount = discount,
            isAlmostGone = isAlmostGone,

            // 👇 Передаем вычисленную картинку (чтобы в UI работало фото оффера)
            imageUrl = finalImageUrl,

            // Передаем весь объект бизнеса
            business = safeBusiness
        )
    }

    // Твой старый добрый парсер времени (он лучше, чем SimpleDateFormat, для простых строк)
    private fun extractTimeFromISO(isoString: String): String? {
        return try {
            // Ищет паттерн "T20:30"
            val regex = Regex("""T(\d{2}:\d{2})""")
            val match = regex.find(isoString)
            // Возвращает "20:30"
            match?.groupValues?.get(1)
        } catch (e: Exception) {
            null
        }
    }
}