package com.biocube.app.data.repository

import com.biocube.app.data.local.dao.LocationDao
import com.biocube.app.data.local.entity.toDomain
import com.biocube.app.data.local.entity.toEntity
import com.biocube.app.data.remote.api.BiocubeApi
import com.biocube.app.data.remote.dto.LocationDto
import com.biocube.app.data.remote.dto.LocationSyncRequest
import com.biocube.app.domain.model.LocationData
import com.biocube.app.domain.repository.ILocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val api: BiocubeApi,
    private val locationDao: LocationDao
) : ILocationRepository {

    override suspend fun saveLocation(location: LocationData) {
        locationDao.insertLocation(location.toEntity())
    }

    override fun getLocationsByUser(userId: String): Flow<List<LocationData>> {
        return locationDao.getLocationsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncLocations(userId: String): Result<Int> {
        return try {
            val unsyncedLocations = locationDao.getUnsyncedLocations(userId)
            
            if (unsyncedLocations.isEmpty()) {
                return Result.success(0)
            }

            val locationDtos = unsyncedLocations.map {
                LocationDto(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    timestamp = it.timestamp,
                    accuracy = it.accuracy
                )
            }

            val response = api.syncLocations(
                LocationSyncRequest(userId, locationDtos)
            )

            if (response.success) {
                val locationIds = unsyncedLocations.map { it.id }
                locationDao.markLocationsAsSynced(locationIds)
                
                // Delete old synced locations (older than 7 days)
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                locationDao.deleteOldSyncedLocations(sevenDaysAgo)
                
                Result.success(response.syncedCount)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnsyncedLocationsCount(userId: String): Int {
        return locationDao.getUnsyncedLocations(userId).size
    }
}
