package com.example.eventconnect.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO = Data Access Object
 * Il définit les opérations que l’on peut faire sur la table des événements.
 */
@Dao
interface EventDao {

    // insérer un événement (remplace s’il existe déjà)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    // récupérer tous les événements (Flux pour maj automatique)
    @Query("SELECT * FROM events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<EventEntity>>

    // supprimer tous les événements (utile pour tests)
    @Query("DELETE FROM events")
    suspend fun deleteAll()
    @Query("SELECT * FROM events")
    suspend fun getAllEventsOnce(): List<EventEntity>
    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)

}

