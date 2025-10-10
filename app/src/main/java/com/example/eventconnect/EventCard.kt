package com.example.eventconnect

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import coil.compose.rememberAsyncImagePainter

@Composable
fun EventList(events: List<Event>, onDelete: (Event) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(events) { event ->
            EventCard(event = event, onDelete = onDelete)
        }
    }
}

@Composable
fun EventCard(event: Event, onDelete: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { onDelete(event) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer"
                    )
                }
            }

            Text(
                text = event.date,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = event.location,
                style = MaterialTheme.typography.bodyMedium
            )

            event.imageUri?.let {
                Text(
                    text = "Image: $it",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


