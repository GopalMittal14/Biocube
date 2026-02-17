package com.biocube.app.presentation.facescan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biocube.app.data.local.dao.FaceScanDao
import com.biocube.app.data.local.entity.FaceScanEntity
import com.biocube.app.domain.model.User
import com.biocube.app.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class FaceScanSaveState {
    object Idle : FaceScanSaveState()
    object Loading : FaceScanSaveState()
    object Success : FaceScanSaveState()
    data class Error(val message: String) : FaceScanSaveState()
}

@HiltViewModel
class FaceScanViewModel @Inject constructor(
    private val faceScanDao: FaceScanDao,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<FaceScanSaveState>(FaceScanSaveState.Idle)
    val saveState: StateFlow<FaceScanSaveState> = _saveState.asStateFlow()

    suspend fun getCurrentUserSync(): User? = userRepository.getCurrentUserSync()

    fun saveFaceScan(imagePath: String) {
        viewModelScope.launch {
            _saveState.value = FaceScanSaveState.Loading
            try {
                val user = userRepository.getCurrentUserSync()
                if (user == null) {
                    _saveState.value = FaceScanSaveState.Error("No user logged in")
                    return@launch
                }
                faceScanDao.deleteFaceScanByUserId(user.id)
                val faceScan = FaceScanEntity(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    imagePath = imagePath,
                    capturedAt = System.currentTimeMillis()
                )
                faceScanDao.insertFaceScan(faceScan)
                _saveState.value = FaceScanSaveState.Success
            } catch (e: Exception) {
                _saveState.value = FaceScanSaveState.Error(e.localizedMessage ?: "Failed to save face scan")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = FaceScanSaveState.Idle
    }
}
