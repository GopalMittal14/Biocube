package com.biocube.core.data.repository

import com.biocube.core.data.remote.api.BiocubeApi
import com.biocube.core.domain.model.BiometricScan
import com.biocube.core.domain.model.ScanType
import com.biocube.core.domain.repository.IBiometricRepository
import com.biocube.core.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricRepository @Inject constructor(
    private val api: BiocubeApi
) : IBiometricRepository {

    override suspend fun getBiometricScans(): Flow<Resource<List<BiometricScan>>> = flow {
        try {
            emit(Resource.Loading())
            
            val scans = api.getBiometricScans().map { dto ->
                BiometricScan(
                    id = dto.id,
                    type = ScanType.valueOf(dto.type),
                    name = dto.name,
                    description = dto.description,
                    isCompleted = dto.isCompleted
                )
            }
            
            emit(Resource.Success(scans))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch biometric scans"))
        }
    }
}
