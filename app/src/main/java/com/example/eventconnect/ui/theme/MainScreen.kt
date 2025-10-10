package com.example.eventconnect.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.eventconnect.data.EventDao
import com.example.eventconnect.data.EventEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    eventDao: EventDao,
    onEventClick: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var showForm by remember { mutableStateOf(false) }

    val events = eventDao.getAllEvents().collectAsState(initial = emptyList())

    // --- Ajouter un événement par défaut si la base est vide ---
    LaunchedEffect(Unit) {
        val count = eventDao.getAllEventsOnce().size
        if (count == 0) {
            eventDao.insertEvent(
                EventEntity(
                    title = "Concert de Rap",
                    date = "2025-10-15",
                    location = "Stade du 4 Août"
                )
            )
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un événement")
            }
        }
    ) { innerPadding ->

        if (showForm) {
            // ✅ Vérifie que tu as bien un fichier CreateEventForm.kt avec cette fonction
            CreateEventForm(
                onEventCreated = { newEvent ->
                    scope.launch {
                        eventDao.insertEvent(
                            EventEntity(
                                title = newEvent.title,
                                date = newEvent.date,
                                location = newEvent.location,
                                imageUri = newEvent.imageUri
                            )
                        )
                    }
                    showForm = false
                }
            )
            BackHandler { showForm = false }
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {
                EventList(
                    events = events.value,
                    onDelete = { eventToDelete ->
                        scope.launch {
                            eventDao.deleteEventById(eventToDelete.id)
                        }
                    },
                    onEventClick = { clickedEvent ->
                        onEventClick(clickedEvent.id)
                    }
                )
            }
        }
    }
}
