package com.biocube.core.data.remote.api

import com.biocube.core.data.remote.dto.*
import retrofit2.http.*

interface BiocubeApi {
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @GET("api/biometric/scans")
    suspend fun getBiometricScans(): List<BiometricScanDto>
    
    @GET("api/services")
    suspend fun getServices(): List<ServiceDto>
    
    @POST("api/location/sync")
    suspend fun syncLocations(@Body request: LocationSyncRequest): SyncResponse
    
    @GET("api/user/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): UserDto
    
    @PUT("api/user/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body user: UserDto
    ): UserDto
}
