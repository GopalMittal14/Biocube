package com.biocube.app.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.biocube.app.domain.model.LocationData
import java.util.UUID

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float,
    val isSynced: Boolean = false
)

fun LocationEntity.toDomain(): LocationData {
    return LocationData(
        id = id,
        userId = userId,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp,
        accuracy = accuracy,
        isSynced = isSynced
    )
}

fun LocationData.toEntity(): LocationEntity {
    return LocationEntity(
        id = id.ifEmpty { UUID.randomUUID().toString() },
        userId = userId,
        latitude = latitude,
        longitude = longitude,
        timestamp = timestamp,
        accuracy = accuracy,
        isSynced = isSynced
    )
}
