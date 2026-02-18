package com.biocube.app.presentation.usertrainings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biocube.core.domain.model.BiometricScan
import com.biocube.core.domain.repository.IBiometricRepository
import com.biocube.core.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserTrainingsViewModel @Inject constructor(
    private val biometricRepository: IBiometricRepository
) : ViewModel() {

    private val _scansState = MutableStateFlow<ScansState>(ScansState.Loading)
    val scansState: StateFlow<ScansState> = _scansState.asStateFlow()

    init {
        loadBiometricScans()
    }

    private fun loadBiometricScans() {
        viewModelScope.launch {
            biometricRepository.getBiometricScans().collect { resource ->
                _scansState.value = when (resource) {
                    is Resource.Loading -> ScansState.Loading
                    is Resource.Success -> ScansState.Success(resource.data ?: emptyList())
                    is Resource.Error -> ScansState.Error(resource.message ?: "Failed to load scans")
                }
            }
        }
    }

    fun onScanClick(scan: BiometricScan) {
        // Handle scan click - could trigger biometric authentication
    }
}

sealed class ScansState {
    object Loading : ScansState()
    data class Success(val scans: List<BiometricScan>) : ScansState()
    data class Error(val message: String) : ScansState()
}
