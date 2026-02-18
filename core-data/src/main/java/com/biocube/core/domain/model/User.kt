package com.biocube.core.domain.model

data class User(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val profileImageUrl: String? = null,
    val isFirstLogin: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncedAt: Long = 0L
)
