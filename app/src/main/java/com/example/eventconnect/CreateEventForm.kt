package com.example.eventconnect

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.eventconnect.data.EventEntity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventForm(onEventCreated: (EventEntity) -> Unit = {}) {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // États
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var dateText by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // --- Catégories ---
    val categories = listOf("Musique", "Théâtre", "Sport", "Conférence", "Autre")
    var selectedCategory by remember { mutableStateOf(categories.last()) }
    var expanded by remember { mutableStateOf(false) }

    // --- Localisation ---
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    // --- Permissions ---
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    latitude = loc.latitude
                    longitude = loc.longitude
                    Toast.makeText(context, "Position détectée ✅", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Impossible d’obtenir la position", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Permission refusée ❌", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Sélecteur de date ---
    val todayCal = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            val selected = Calendar.getInstance().apply { set(y, m, d) }
            if (selected.before(todayCal)) {
                dateError = "La date doit être aujourd'hui ou ultérieure"
            } else {
                dateError = null
                dateText = "$y-${(m + 1).toString().padStart(2, '0')}-$d"
            }
        },
        todayCal.get(Calendar.YEAR),
        todayCal.get(Calendar.MONTH),
        todayCal.get(Calendar.DAY_OF_MONTH)
    )

    // --- Sélecteur d’image ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    // --- UI principale ---
    Column(modifier = Modifier.padding(16.dp)) {

        // Champ titre
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre *") },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError != null
        )
        titleError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(8.dp))

        // Champ date
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            label = { Text("Date *") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePicker.show() },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { datePicker.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Choisir la date")
                }
            },
            isError = dateError != null
        )
        dateError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Spacer(modifier = Modifier.height(8.dp))

        // Champ lieu
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Lieu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Sélection de catégorie fonctionnelle
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                label = { Text("Catégorie") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
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

        Spacer(modifier = Modifier.height(12.dp))

        // 🌍 Localisation
        Button(
            onClick = {
                val permission = Manifest.permission.ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            latitude = loc.latitude
                            longitude = loc.longitude
                            Toast.makeText(context, "Position détectée ✅", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Impossible d’obtenir la position", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(permission)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📍 Utiliser ma position actuelle")
        }

        latitude?.let { Text("Latitude : $latitude") }
        longitude?.let { Text("Longitude : $longitude") }

        Spacer(modifier = Modifier.height(12.dp))

        // Image
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choisir une image")
        }

        imageUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton Créer
        Button(
            onClick = {
                var valid = true
                if (title.isBlank()) {
                    titleError = "Le titre est obligatoire"
                    valid = false
                }
                if (dateText.isBlank()) {
                    dateError = "La date est obligatoire"
                    valid = false
                }

                if (valid) {
                    val event = EventEntity(
                        title = title,
                        date = dateText,
                        location = location,
                        description = description,
                        imageUri = imageUri?.toString(),
                        category = selectedCategory,
                        latitude = latitude,
                        longitude = longitude
                    )
                    onEventCreated(event)
                    Toast.makeText(context, "Événement créé ✅", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Créer l’événement")
        }
    }
}
