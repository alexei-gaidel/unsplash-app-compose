package com.example.imaginarium.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PhotoDBEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}