package com.biocube.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "face_scans",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"], unique = true)]
)
data class FaceScanEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val imagePath: String,
    /**
     * L2-normalized face embedding produced by the on-device TFLite model.
     * Stored as little-endian float32 bytes.
     */
    val embedding: ByteArray?,
    val capturedAt: Long
)
