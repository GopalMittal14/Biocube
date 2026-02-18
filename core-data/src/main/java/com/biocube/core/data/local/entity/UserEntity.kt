package com.biocube.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.biocube.core.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val profileImageUrl: String?,
    val isFirstLogin: Boolean,
    val createdAt: Long,
    val lastSyncedAt: Long
)

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        username = username,
        email = email,
        fullName = fullName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        isFirstLogin = isFirstLogin,
        createdAt = createdAt,
        lastSyncedAt = lastSyncedAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        username = username,
        email = email,
        fullName = fullName,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
        isFirstLogin = isFirstLogin,
        createdAt = createdAt,
        lastSyncedAt = lastSyncedAt
    )
}
