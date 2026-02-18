package com.biocube.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.biocube.core.data.local.entity.FaceScanEntity

@Dao
interface FaceScanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFaceScan(faceScan: FaceScanEntity)

    @Query("SELECT * FROM face_scans WHERE userId = :userId LIMIT 1")
    suspend fun getFaceScanByUserId(userId: String): FaceScanEntity?

    @Query("DELETE FROM face_scans WHERE userId = :userId")
    suspend fun deleteFaceScanByUserId(userId: String)
}
