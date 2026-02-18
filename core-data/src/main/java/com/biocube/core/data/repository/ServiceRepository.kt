package com.biocube.core.data.repository

import com.biocube.core.data.remote.api.BiocubeApi
import com.biocube.core.domain.model.Service
import com.biocube.core.domain.model.ServiceType
import com.biocube.core.domain.repository.IServiceRepository
import com.biocube.core.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServiceRepository @Inject constructor(
    private val api: BiocubeApi
) : IServiceRepository {

    override suspend fun getServices(): Flow<Resource<List<Service>>> = flow {
        try {
            emit(Resource.Loading())
            
            val services = api.getServices().map { dto ->
                Service(
                    id = dto.id,
                    name = dto.name,
                    description = dto.description,
                    type = ServiceType.valueOf(dto.type)
                )
            }
            
            emit(Resource.Success(services))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to fetch services"))
        }
    }
}
