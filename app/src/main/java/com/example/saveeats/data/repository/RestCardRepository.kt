package com.example.saveeats.data.repository

import com.example.saveeats.data.models.Offer
import com.example.saveeats.data.api.RetrofitClient
import com.example.saveeats.data.models.offers.Business
import android.util.Log

class RestCardRepository {

    // Сервисы API
    private val businessService = RetrofitClient.businessService
    private val offerService = RetrofitClient.offerService

    /**
     * Получить офферы (рестораны с предложениями)
     */
    suspend fun getRestCards(
        lat: Double = 56.9972,
        lon: Double = 40.9714,
        businessId: Int? = null,
        onlyAvailable: Boolean = false
    ): List<Offer> {
        return try {
            Log.d("RestRepo", "🚀 Запрос офферов: lat=$lat, lon=$lon")

            // 1. Получаем офферы из API
            val offersResponse = offerService.getOffers(
                businessId = businessId,
                onlyAvailable = onlyAvailable
            )

            Log.d("RestRepo", "✅ Ответ сервера получен. Найдено офферов: ${offersResponse.size}")

            // 👇 ГЛАВНОЕ ИСПРАВЛЕНИЕ 👇
            // Если сервер ответил "200 OK", но список пустой -> возвращаем моки, чтобы ты мог работать дальше
            if (offersResponse.isEmpty()) {
                Log.w("RestRepo", "⚠️ Список пуст! Возвращаю тестовые данные (Mocks).")
                return getMockOffers()
            }

            // 2. Получаем ближайшие бизнесы (для данных, которых нет в оффере)
            val businesses = try {
                businessService.getNearby(lat, lon)
            } catch (e: Exception) {
                Log.e("RestRepo", "Ошибка загрузки бизнесов: ${e.message}")
                emptyList<Business>()
            }

            val businessMap = businesses.associateBy { it.id }

            // 3. Собираем итоговый список
            offersResponse.map { offerResponse ->
                // Пытаемся найти бизнес:
                // Приоритет 1: Вложенный бизнес (если бэкенд его прислал)
                // Приоритет 2: Из отдельного запроса getNearby
                val business = offerResponse.business ?: businessMap[offerResponse.businessId]

                if (business == null) {
                    Log.e(
                        "RestRepo",
                        "❌ Не найден бизнес для оффера ID=${offerResponse.id} (BusinessID=${offerResponse.businessId})"
                    )
                }

                OfferMapper.toOffer(offerResponse, business)
            }
        } catch (e: Exception) {
            Log.e("RestRepo", "🔥 ОШИБКА ЗАГРУЗКИ: ${e.message}")
            e.printStackTrace()
            // В случае ошибки сети тоже возвращаем моки
            getMockOffers()
        }
    }

    /**
     * Получить конкретный оффер по ID
     */
    suspend fun getOffer(offerId: Int): Offer? {
        return try {
            val offerResponse = offerService.getOffer(offerId)
            // Берем бизнес прямо из ответа
            OfferMapper.toOffer(offerResponse, offerResponse.business)
        } catch (e: Exception) {
            Log.e("RestRepo", "Ошибка загрузки деталей: ${e.message}")
            e.printStackTrace()
            getMockOffers().find { it.id == offerId }
        }
    }

    // 👇 МОКОВЫЕ ДАННЫЕ (Оставь как есть) 👇
    // 👇 ИСПРАВЛЕННАЯ ФУНКЦИЯ MOCK DATA (БЕЗ imageUrl) 👇
    private fun getMockOffers(): List<Offer> {
        val mockBusiness1 = Business(
            id = 1,
            owner_id = 0,
            name = "Ресторан 1 (Тест)",
            address = "Ул. Ленина, 1",
            latitude = 0.0,
            longitude = 0.0,
            logo_url = null,
            cover_image_url = "https://placehold.co/400x300/8B4545/FFFFFF?text=Restaurant+1",
            rating = 4.5,
            distance_km = 1.5
        )

        val mockBusiness2 = Business(
            id = 2,
            owner_id = 0,
            name = "Ресторан 2 (Тест)",
            address = "Ул. Пушкина, 10",
            latitude = 0.0,
            longitude = 0.0,
            logo_url = null,
            cover_image_url = "https://placehold.co/400x300/6B3535/FFFFFF?text=Restaurant+2",
            rating = 4.8,
            distance_km = 2.3
        )

        val mockBusiness3 = Business(
            id = 3,
            owner_id = 0,
            name = "Ресторан 3 (Тест)",
            address = "Пр. Мира, 5",
            latitude = 0.0,
            longitude = 0.0,
            logo_url = null,
            cover_image_url = "https://placehold.co/400x300/8B4545/FFFFFF?text=Restaurant+3",
            rating = 4.2,
            distance_km = 0.8
        )

        return listOf(
            Offer(
                id = 1,
                name = "Magic Bag: Выпечка",
                category = "Еда",
                time = "15-20 мин",
                boxesLeft = 10,
                oldPrice = 500,
                newPrice = 300,
                discount = 40,
                isAlmostGone = false,
                business = mockBusiness1,
                imageUrl = null
            ),
            Offer(
                id = 2,
                name = "Сюрприз бокс",
                category = "Еда",
                time = "20-25 мин",
                boxesLeft = 3,
                oldPrice = 600,
                newPrice = 350,
                discount = 42,
                isAlmostGone = true,
                business = mockBusiness2,
                imageUrl = null
            ),
            Offer(
                id = 3,
                name = "Ланч бокс",
                category = "Еда",
                time = "10-15 мин",
                boxesLeft = 15,
                oldPrice = 450,
                newPrice = 280,
                discount = 38,
                isAlmostGone = false,
                business = mockBusiness3,
                imageUrl = null
            )
        )
    }
}