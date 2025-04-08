package com.example.reverseclassroomdemo.students

import android.util.Log
import androidx.compose.runtime.Composable
import com.example.reverseclassroomdemo.data.Student


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Composable
fun StudentsScreen() {
    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val client = remember {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                val text = client.get("http://10.0.2.2:8080/students").bodyAsText()
                Json.decodeFromString<List<Student>>(text)
            }
            Log.d("StudentsScreen", "Response: $response")
            students = response
        } catch (e: Exception) {
            errorMessage = "Failed to load students: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        errorMessage != null -> {
            Text(
                text = errorMessage ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        else -> {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(students) { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${student.name} ${student.surname}", style = MaterialTheme.typography.titleMedium)
                            Text("DOB: ${student.dateOfBirth}")
                            Text("Gender: ${if (student.gender) "Male" else "Female"}")
                            Text("Grade: ${student.grade}")
                        }
                    }
                }
            }
        }
    }
}