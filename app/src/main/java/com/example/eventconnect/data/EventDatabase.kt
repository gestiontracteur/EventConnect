package com.example.eventconnect.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventEntity::class], version = 2, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                )
                    // ⚠️ Ces deux lignes empêchent les erreurs de migration
                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
