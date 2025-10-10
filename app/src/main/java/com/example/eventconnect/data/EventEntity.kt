package com.example.eventconnect.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val location: String,
    val imageUri: String? = null
)
