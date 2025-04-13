package com.example.reverseclassroomdemo.movies

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.reverseclassroomdemo.data.Movie
import com.example.reverseclassroomdemo.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

@Composable
fun MoviesScreen() {
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }


    val client = remember {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {}
        }
    }

    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                val rawResponse = client.get("https://api.themoviedb.org/3/movie/popular") {
                    parameter("api_key", BuildConfig.TMDB_API_KEY)
                }.bodyAsText()

                val json = Json { ignoreUnknownKeys = true }
                val jsonObject = json.parseToJsonElement(rawResponse).jsonObject
                val resultsArray = jsonObject["results"]?.jsonArray?:throw Exception("Missing results")
                resultsArray.map { json.decodeFromJsonElement<Movie>(it) }
            }

            Log.d("MoviesScreen", "Loaded ${response.size} movies")
            movies = response
        } catch (e: Exception) {
            errorMessage = "Failed to load movies: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
            Column(modifier = Modifier.fillMaxSize()) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                val filteredMovies = movies.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }


                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(filteredMovies) { movie ->
                        var isExpanded by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { isExpanded = !isExpanded },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    movie.title,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Image(
                                    painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Overview: ${movie.overview}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "Release Date: ${movie.release_date}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "Rating: ${"%.1f".format(movie.vote_average)}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                } else {
                                    Text(
                                        text = movie.overview.take(100) + "...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

