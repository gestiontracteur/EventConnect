package com.example.eventconnect.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.eventconnect.CreateEventForm
import com.example.eventconnect.data.EventDao
import com.example.eventconnect.utils.calculateDistance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    eventDao: EventDao,
    onEventClick: (Int) -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val scope = rememberCoroutineScope()

    var showForm by remember { mutableStateOf(false) }
    val events = eventDao.getAllEvents().collectAsState(initial = emptyList())

    // Position actuelle
    var currentLat by remember { mutableStateOf<Double?>(null) }
    var currentLon by remember { mutableStateOf<Double?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    currentLat = loc.latitude
                    currentLon = loc.longitude
                    Toast.makeText(context, "Position actuelle détectée ✅", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Impossible d’obtenir la position", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Permission localisation refusée ❌", Toast.LENGTH_SHORT).show()
        }
    }

    // Récupération automatique de la position
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                currentLat = loc?.latitude
                currentLon = loc?.longitude
            }
        } else {
            locationPermissionLauncher.launch(permission)
        }
    }

    Scaffold(
        topBar = {
            // ✅ Utilisation de TopAppBar (remplace SmallTopAppBar)
            TopAppBar(
                title = { Text("EventConnect") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.WbSunny else Icons.Default.Brightness2,
                            contentDescription = "Changer de thème"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un événement")
            }
        }
    ) { innerPadding ->
        if (showForm) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(8.dp)
            ) {
                Button(
                    onClick = { showForm = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("⬅️ Retour")
                }

                CreateEventForm(
                    onEventCreated = { newEvent ->
                        scope.launch { eventDao.insertEvent(newEvent) }
                        showForm = false
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp)
            ) {
                // Bouton pour filtrer les événements proches
                Button(
                    onClick = {
                        val permission = Manifest.permission.ACCESS_FINE_LOCATION
                        if (ContextCompat.checkSelfPermission(context, permission)
                            == PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                if (loc != null) {
                                    currentLat = loc.latitude
                                    currentLon = loc.longitude
                                    Toast.makeText(context, "Position mise à jour ✅", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Impossible d’obtenir la position", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            locationPermissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text("📍 Afficher les événements proches (≤ 10 km)")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filtre par catégorie
                val categories = listOf("Toutes", "Musique", "Théâtre", "Sport", "Conférence", "Autre")
                var selectedCategory by remember { mutableStateOf(categories.first()) }
                var expanded by remember { mutableStateOf(false) }

                Text("🎯 Filtrer par catégorie", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        label = { Text("Catégorie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (selectedCategory != "Toutes") {
                    Button(
                        onClick = { selectedCategory = "Toutes" },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("🔁 Réinitialiser le filtre")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val nearbyEvents = if (currentLat != null && currentLon != null) {
                    events.value.filter {
                        it.latitude != null && it.longitude != null &&
                                calculateDistance(
                                    currentLat!!,
                                    currentLon!!,
                                    it.latitude!!,
                                    it.longitude!!
                                ) <= 10.0
                    }
                } else emptyList()

                val displayedEvents = when {
                    nearbyEvents.isNotEmpty() && selectedCategory != "Toutes" ->
                        nearbyEvents.filter { it.category == selectedCategory }
                    selectedCategory != "Toutes" ->
                        events.value.filter { it.category == selectedCategory }
                    nearbyEvents.isNotEmpty() -> nearbyEvents
                    else -> events.value
                }

                EventList(
                    events = displayedEvents,
                    onDelete = { eventToDelete ->
                        scope.launch { eventDao.deleteEventById(eventToDelete.id) }
                    },
                    onEventClick = { event -> onEventClick(event.id) }
                )
            }
        }
    }
}
