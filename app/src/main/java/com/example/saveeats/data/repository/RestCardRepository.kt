package com.example.saveeats.data.repository
import com.example.saveeats.data.models.Offer
import com.example.saveeats.data.api.RetrofitClient

class RestCardRepository {

    // Используем наш новый сервис из клиента
    private val api = RetrofitClient.businessService

    suspend fun getRestCards(lat: Double = 56.9972, lon: Double = 40.9714): List<Offer> {
        return try {
            val response = api.getNearby(lat, lon)

            response.map { dto ->
                // Mock данные для полей, которых нет в Business
                val mockOldPrice = 500
                val mockNewPrice = 300
                val mockDiscount = mockOldPrice - mockNewPrice
                val mockBoxesLeft = (5..20).random() // Случайное количество от 5 до 20
                val mockTime = "15-20 мин" // Время доставки
                val mockIsAlmostGone = mockBoxesLeft <= 3 // Почти закончилось, если осталось 3 или меньше

                Offer(
                    id = dto.id,
                    name = dto.name,
                    category = "Еда", // На бэке пока нет категории, хардкодим
                    distance = dto.distance_km.toFloat(), // Конвертируем Double в Float
                    time = mockTime,
                    boxesLeft = mockBoxesLeft,
                    oldPrice = mockOldPrice,
                    newPrice = mockNewPrice,
                    discount = mockDiscount,
                    rating = dto.rating ?: 0.0,
                    isAlmostGone = mockIsAlmostGone
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // В случае ошибки возвращаем mock данные для тестирования
            getMockOffers()
        }
    }

    // Функция для возврата mock данных при ошибке сети
    private fun getMockOffers(): List<Offer> {
        return listOf(
            Offer(
                id = 1,
                name = "Ресторан 1",
                category = "Еда",
                distance = 1.5f,
                time = "15-20 мин",
                boxesLeft = 10,
                oldPrice = 500,
                newPrice = 300,
                discount = 200,
                rating = 4.5,
                isAlmostGone = false
            ),
            Offer(
                id = 2,
                name = "Ресторан 2",
                category = "Еда",
                distance = 2.3f,
                time = "20-25 мин",
                boxesLeft = 3,
                oldPrice = 600,
                newPrice = 350,
                discount = 250,
                rating = 4.8,
                isAlmostGone = true
            ),
            Offer(
                id = 3,
                name = "Ресторан 3",
                category = "Еда",
                distance = 0.8f,
                time = "10-15 мин",
                boxesLeft = 15,
                oldPrice = 450,
                newPrice = 280,
                discount = 170,
                rating = 4.2,
                isAlmostGone = false
            )
        )
    }
}

