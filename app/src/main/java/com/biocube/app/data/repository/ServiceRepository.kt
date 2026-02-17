package com.biocube.app.data.repository

import com.biocube.app.data.remote.api.BiocubeApi
import com.biocube.app.domain.model.Service
import com.biocube.app.domain.model.ServiceType
import com.biocube.app.domain.repository.IServiceRepository
import com.biocube.app.util.Resource
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
