package com.example.saveeats.data.repository

import com.example.saveeats.data.models.ProfileData

class profileRepository {

    fun getProfileData(): ProfileData{
        return ProfileData(
            name = "Матвей Недбайло",
            email = "matvey.nedbailo@gmail.com",
            savedBoxes = 12,
            moneySaved = 6750,
            co2Saved = 46,
            favoriteCount = 4,
            ordersCount = 12,
            address = "г.Иваново, ул.Шубиных, д.27"

        )


    }

}