package com.biocube.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biocube.core.domain.model.Service
import com.biocube.core.domain.repository.IServiceRepository
import com.biocube.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val serviceRepository: IServiceRepository
) : ViewModel() {

    private val _servicesState = MutableStateFlow<ServicesState>(ServicesState.Loading)
    val servicesState: StateFlow<ServicesState> = _servicesState.asStateFlow()

    init {
        loadServices()
    }

    private fun loadServices() {
        viewModelScope.launch {
            serviceRepository.getServices().collect { resource ->
                _servicesState.value = when (resource) {
                    is Resource.Loading -> ServicesState.Loading
                    is Resource.Success -> ServicesState.Success(resource.data ?: emptyList())
                    is Resource.Error -> ServicesState.Error(resource.message ?: "Failed to load services")
                }
            }
        }
    }

    fun onServiceClick(service: Service) {
        // Handle service click
    }
}

sealed class ServicesState {
    object Loading : ServicesState()
    data class Success(val services: List<Service>) : ServicesState()
    data class Error(val message: String) : ServicesState()
}
