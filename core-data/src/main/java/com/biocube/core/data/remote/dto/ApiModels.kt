package com.biocube.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserDto?
)

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("profileImageUrl")
    val profileImageUrl: String?,
    @SerializedName("isFirstLogin")
    val isFirstLogin: Boolean
)

data class BiometricScanDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("isCompleted")
    val isCompleted: Boolean
)

data class ServiceDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("type")
    val type: String
)

data class LocationSyncRequest(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("locations")
    val locations: List<LocationDto>
)

data class LocationDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("accuracy")
    val accuracy: Float
)

data class SyncResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("syncedCount")
    val syncedCount: Int
)
