package com.example.reverseclassroomdemo.data

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val title: String,
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val vote_average: Double
)