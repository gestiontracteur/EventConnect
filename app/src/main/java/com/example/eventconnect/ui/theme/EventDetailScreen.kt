package com.example.eventconnect.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.EventDao
import com.example.eventconnect.data.EventEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventDao: EventDao,
    eventId: Int,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var event by remember { mutableStateOf<EventEntity?>(null) }
    val context = LocalContext.current

    // Charger l'événement depuis la base
    LaunchedEffect(eventId) {
        event = eventDao.getEventById(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event?.title ?: "Détails de l’événement") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val isParticipating = remember { mutableStateOf(event!!.isParticipating) }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                event?.imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = event?.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Titre : ${event?.title}", style = MaterialTheme.typography.headlineSmall)
                Text("Date : ${event?.date}", style = MaterialTheme.typography.bodyLarge)
                Text("Lieu : ${event?.location}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Catégorie : ${event?.category}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Text(event?.description ?: "Aucune description", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))

                // ✅ Bouton Participer / Déjà inscrit
                Button(
                    onClick = {
                        scope.launch {
                            val newParticipation = !isParticipating.value
                            eventDao.updateParticipation(event!!.id, newParticipation)
                            event = eventDao.getEventById(eventId)
                            isParticipating.value = newParticipation

                            val message = if (newParticipation)
                                "✅ Vous participez à cet événement !"
                            else
                                "❌ Vous ne participez plus à cet événement."
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isParticipating.value)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isParticipating.value) "✅ Déjà inscrit (annuler)"
                        else "🎟️ Participer"
                    )
                }
            }
        }
    }
}
