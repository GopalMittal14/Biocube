package com.biocube.app.data.local.dao

import androidx.room.*
import com.biocube.app.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    
    @Query("SELECT * FROM locations WHERE userId = :userId ORDER BY timestamp DESC")
    fun getLocationsByUser(userId: String): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE userId = :userId AND isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedLocations(userId: String): List<LocationEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)
    
    @Query("UPDATE locations SET isSynced = 1 WHERE id IN (:locationIds)")
    suspend fun markLocationsAsSynced(locationIds: List<String>)
    
    @Query("DELETE FROM locations WHERE timestamp < :timestamp AND isSynced = 1")
    suspend fun deleteOldSyncedLocations(timestamp: Long)
    
    @Query("DELETE FROM locations WHERE userId = :userId")
    suspend fun deleteLocationsByUser(userId: String)
}
