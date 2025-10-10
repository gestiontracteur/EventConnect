package com.example.eventconnect

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun CreateEventForm(onEventCreated: (Event) -> Unit = {}) {
    val context = LocalContext.current

    // États des champs
    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var dateText by remember { mutableStateOf("") }
    var dateError by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // DatePickerDialog
    val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            val selected = Calendar.getInstance().apply {
                set(y, m, d, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (selected.before(todayCal)) {
                dateError = "La date doit être aujourd'hui ou ultérieure"
            } else {
                dateError = null
                dateText = "${y}-${(m + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
            }
        },
        todayCal.get(Calendar.YEAR),
        todayCal.get(Calendar.MONTH),
        todayCal.get(Calendar.DAY_OF_MONTH)
    )

    // --- UI du formulaire ---
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titre *") },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError != null
        )
        if (titleError != null) {
            Text(
                text = titleError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
        if (dateError != null) {
            Text(
                text = dateError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Lieu") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6
        )
        // --- Sélecteur d’image ---
        var imageUri by remember { mutableStateOf<Uri?>(null) }

// Lanceur d’activité pour ouvrir la galerie
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageUri = uri
        }

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
                contentDescription = "Image choisie",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                var valid = true
                if (title.isBlank()) {
                    titleError = "Le titre est obligatoire"
                    valid = false
                } else titleError = null

                if (dateText.isBlank()) {
                    dateError = "La date est obligatoire"
                    valid = false
                }

                if (valid) {
                    val newEvent = Event(
                        title = title,
                        date = dateText,
                        location = location,
                        imageUri = imageUri?.toString()
                    )
                    onEventCreated(newEvent)
                    Toast.makeText(context, "Événement créé", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Créer l’événement")
        }
            Text("Créer l’événement")
        }
    }

