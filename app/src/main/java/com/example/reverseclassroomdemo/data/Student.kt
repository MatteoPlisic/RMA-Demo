package com.example.reverseclassroomdemo.data

import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val name: String,
    val surname: String,
    val dateOfBirth: String,
    val gender: Boolean,
    val grade: Double
)