package com.biocube.core.domain.repository

import com.biocube.core.domain.model.BiometricScan
import com.biocube.core.domain.model.LocationData
import com.biocube.core.domain.model.Service
import com.biocube.core.domain.model.User
import com.biocube.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun login(username: String, password: String): Flow<Resource<User>>
    fun getCurrentUser(): Flow<User?>
    suspend fun getCurrentUserSync(): User?
    suspend fun updateUser(user: User)
    suspend fun updateFirstLoginStatus(userId: String, isFirstLogin: Boolean)
    suspend fun logout()
    suspend fun saveUser(user: User)
}

interface ILocationRepository {
    suspend fun saveLocation(location: LocationData)
    fun getLocationsByUser(userId: String): Flow<List<LocationData>>
    suspend fun syncLocations(userId: String): Result<Int>
    suspend fun getUnsyncedLocationsCount(userId: String): Int
}

interface IBiometricRepository {
    suspend fun getBiometricScans(): Flow<Resource<List<BiometricScan>>>
}

interface IServiceRepository {
    suspend fun getServices(): Flow<Resource<List<Service>>>
}
