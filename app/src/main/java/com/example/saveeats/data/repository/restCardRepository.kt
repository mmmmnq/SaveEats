package com.example.saveeats.data.repository
import com.example.saveeats.data.models.Offer


class restCardRepository {

    fun getRestCards(): List<Offer> = listOf(
        Offer(
            id = 1,
            name = "St.Bunny",
            category = "Европейская кухня",
            time = "20:30 - 21:00",
            distance = 0.8f,
            boxesLeft = 5,
            oldPrice = 1000,
            newPrice = 444,
            discount = 55,
            rating = 4.8,
            isAlmostGone = false
        ),
        Offer(
            id = 2,
            name = "Печь",
            category = "Итальянская кухня",
            time = "20:30 - 21:00",
            distance = 1.4f,
            boxesLeft = 2,
            oldPrice = 1300,
            newPrice = 400,
            discount = 70,
            rating = 4.3,
            isAlmostGone = true




        ),
        Offer(
            id = 3,
            name = "Цони Мацони",
            category = "Грузинская кухня",
            time = "21:30 - 22:00",
            distance = 1.2f,
            boxesLeft = 6,
            oldPrice = 1444,
            newPrice = 550,
            discount = 61,
            rating = 4.7,
            isAlmostGone = false




        ),
        Offer(
            id = 4,
            name = "Незвания не придумал",
            category = "Русская кухня",
            time = "21:30 - 22:00",
            distance = 1.2f,
            boxesLeft = 6,
            oldPrice = 1444,
            newPrice = 550,
            discount = 61,
            rating = 3.3,
            isAlmostGone = false
        )


    )
}
