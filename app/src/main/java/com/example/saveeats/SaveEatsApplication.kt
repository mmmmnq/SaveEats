package com.example.saveeats

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class SaveEatsApplication : Application() {
    companion object {
        lateinit var instance: SaveEatsApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("a82066e3-d04e-49bc-a52f-ed30d5b1d6cc")
        MapKitFactory.initialize(this)
        instance = this
    }
}