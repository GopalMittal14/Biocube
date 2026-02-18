package com.biocube.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.biocube.core.data.local.dao.FaceScanDao
import com.biocube.core.data.local.dao.LocationDao
import com.biocube.core.data.local.dao.UserDao
import com.biocube.core.data.local.entity.FaceScanEntity
import com.biocube.core.data.local.entity.LocationEntity
import com.biocube.core.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        LocationEntity::class,
        FaceScanEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BiocubeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun locationDao(): LocationDao
    abstract fun faceScanDao(): FaceScanDao
}
