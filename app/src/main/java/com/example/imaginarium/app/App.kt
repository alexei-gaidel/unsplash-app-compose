package com.example.imaginarium.app

import android.app.Application
import androidx.room.Room
import com.example.imaginarium.auth.network.Networking
import com.example.imaginarium.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        Networking.init(this)
    }

    companion object {
        lateinit var INSTANCE: App
            private set
    }
}
