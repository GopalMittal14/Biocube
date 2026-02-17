package com.biocube.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.biocube.app.data.local.dao.FaceScanDao
import com.biocube.app.data.local.dao.LocationDao
import com.biocube.app.data.local.dao.UserDao
import com.biocube.app.data.local.entity.FaceScanEntity
import com.biocube.app.data.local.entity.LocationEntity
import com.biocube.app.data.local.entity.UserEntity

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
