package com.biocube.app.data.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.biocube.app.domain.model.LocationData
import com.biocube.app.domain.repository.ILocationRepository
import com.biocube.app.domain.repository.IUserRepository
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: ILocationRepository,
    private val userRepository: IUserRepository
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        30 * 60 * 1000L // 30 minutes
    ).apply {
        setMinUpdateIntervalMillis(15 * 60 * 1000L) // 15 minutes minimum
        setWaitForAccurateLocation(false)
    }.build()

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                saveLocation(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun saveLocation(location: Location) {
        serviceScope.launch {
            try {
                val user = userRepository.getCurrentUserSync()
                user?.let {
                    val locationData = LocationData(
                        userId = it.id,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestamp = System.currentTimeMillis(),
                        accuracy = location.accuracy
                    )
                    locationRepository.saveLocation(locationData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
}
