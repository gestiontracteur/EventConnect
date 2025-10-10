package com.example.eventconnect.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Base de données Room pour l’application EventConnect
 */
@Database(entities = [EventEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
