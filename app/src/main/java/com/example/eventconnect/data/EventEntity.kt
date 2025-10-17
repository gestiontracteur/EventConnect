package com.example.eventconnect.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "imageUri") val imageUri: String? = null,
    @ColumnInfo(name = "category") val category: String? = "Autre",

    @ColumnInfo(name = "latitude") val latitude: Double? = null,
    @ColumnInfo(name = "longitude") val longitude: Double? = null,
    @ColumnInfo(name = "isParticipating") val isParticipating: Boolean = false
)
