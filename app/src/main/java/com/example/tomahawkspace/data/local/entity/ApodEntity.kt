package com.example.tomahawkspace.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apods")
data class ApodEntity(
    @PrimaryKey
    val date: String, // Дата уникальна для каждого APOD
    val title: String,
    val url: String,
    val explanation: String,
    val mediaType: String,
    val hdUrl: String?
)
