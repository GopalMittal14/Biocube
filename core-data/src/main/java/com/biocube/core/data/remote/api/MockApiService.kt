package com.biocube.core.data.remote.api

import android.content.Context
import com.biocube.core.data.remote.dto.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockApiService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : BiocubeApi {

    override suspend fun login(request: LoginRequest): LoginResponse {
        return withContext(Dispatchers.IO) {
            delay(1000) // Simulate network delay
            
            val users = readFromAssets<List<UserDto>>("mock_users.json")
            val user = users?.find { 
                it.username == request.username 
            }
            
            if (user != null) {
                LoginResponse(
                    success = true,
                    message = "Login successful",
                    user = user
                )
            } else {
                LoginResponse(
                    success = false,
                    message = "Invalid credentials",
                    user = null
                )
            }
        }
    }

    override suspend fun getBiometricScans(): List<BiometricScanDto> {
        return withContext(Dispatchers.IO) {
            delay(500)
            readFromAssets<List<BiometricScanDto>>("mock_biometric_scans.json") ?: emptyList()
        }
    }

    override suspend fun getServices(): List<ServiceDto> {
        return withContext(Dispatchers.IO) {
            delay(500)
            readFromAssets<List<ServiceDto>>("mock_services.json") ?: emptyList()
        }
    }

    override suspend fun syncLocations(request: LocationSyncRequest): SyncResponse {
        return withContext(Dispatchers.IO) {
            delay(1000)
            SyncResponse(
                success = true,
                message = "Locations synced successfully",
                syncedCount = request.locations.size
            )
        }
    }

    override suspend fun getUserProfile(userId: String): UserDto {
        return withContext(Dispatchers.IO) {
            delay(500)
            val users = readFromAssets<List<UserDto>>("mock_users.json")
            users?.find { it.id == userId } ?: throw IOException("User not found")
        }
    }

    override suspend fun updateUserProfile(userId: String, user: UserDto): UserDto {
        return withContext(Dispatchers.IO) {
            delay(500)
            user
        }
    }

    private inline fun <reified T> readFromAssets(fileName: String): T? {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            gson.fromJson(jsonString, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
