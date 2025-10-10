package com.example.eventconnect.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.EventEntity

@Composable
fun EventList(
    events: List<EventEntity>,
    onDelete: (EventEntity) -> Unit,
    onEventClick: (EventEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        if (events.isEmpty()) {
            Text(
                text = "Aucun événement pour le moment.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            events.forEach { event ->
                EventCard(
                    event = event,
                    onDelete = onDelete,
                    onClick = { onEventClick(event) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    event: EventEntity,
    onDelete: (EventEntity) -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            event.imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = event.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = "📅 ${event.date}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "📍 ${event.location}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onDelete(event) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Supprimer")
            }
        }
    }
}
