package com.biocube.app.domain.model

data class LocationData(
    val id: String = "",
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val accuracy: Float = 0f,
    val isSynced: Boolean = false
)
