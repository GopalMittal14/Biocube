package com.biocube.app.domain.repository
import com.biocube.app.domain.model.BiometricScan
import com.biocube.app.domain.model.LocationData
import com.biocube.app.domain.model.Service
import com.biocube.app.domain.model.User
import com.biocube.app.util.Resource
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
