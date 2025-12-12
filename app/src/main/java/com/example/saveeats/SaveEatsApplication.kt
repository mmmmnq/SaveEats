package com.example.saveeats

import android.app.Application

class SaveEatsApplication : Application() {
    companion object {
        lateinit var instance: SaveEatsApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}