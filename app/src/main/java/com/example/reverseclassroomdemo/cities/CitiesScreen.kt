package com.example.reverseclassroomdemo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reverseclassroomdemo.data.City
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CitiesScreen() {
    var cities by remember { mutableStateOf<List<City>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var populationText by remember { mutableStateOf("") }
    var isCapital by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()


    DisposableEffect(Unit) {
        val listener = db.collection("City")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    errorMessage = "Error: ${error.localizedMessage}"
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val cityList = snapshot.documents.mapNotNull { it.toObject(City::class.java) }
                    cities = cityList
                }
            }

        onDispose { listener.remove() }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Cities", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cities) { city ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${city.name}, ${city.country}", style = MaterialTheme.typography.titleMedium)
                        Text("Population: ${city.population}")
                        Text("Capital: ${if (city.isCapital) "Yes" else "No"}")
                    }
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Add New City", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = country,
            onValueChange = { country = it },
            label = { Text("Country") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = populationText,
            onValueChange = { populationText = it },
            label = { Text("Population") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isCapital, onCheckedChange = { isCapital = it })
            Text("Is Capital?")
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val population = populationText.toIntOrNull()
                if (name.isBlank() || country.isBlank() || population == null) {
                    errorMessage = "Please enter valid data."
                    return@Button
                }

                val newCity = City(name, country, population, isCapital)
                isSaving = true

                db.collection("City")
                    .add(newCity)
                    .addOnSuccessListener {
                        errorMessage = null
                        isSaving = false
                        name = ""
                        country = ""
                        populationText = ""
                        isCapital = false
                    }
                    .addOnFailureListener {
                        errorMessage = "Failed to save: ${it.localizedMessage}"
                        isSaving = false
                    }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isSaving) "Saving..." else "Add City")
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}
