package com.biocube.auth.face.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biocube.core.data.faceauth.FaceAuthService
import com.biocube.core.data.faceauth.FaceModelUnavailableException
import com.biocube.core.data.faceauth.FaceVerificationResult
import com.biocube.core.data.faceauth.NoFaceDetectedException
import com.biocube.core.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FaceAuthState {
    data object Idle : FaceAuthState()
    data object Loading : FaceAuthState()
    data object Success : FaceAuthState()
    data class Error(val message: String) : FaceAuthState()
}

@HiltViewModel
class FaceAuthViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val faceAuthService: FaceAuthService
) : ViewModel() {

    private val _state = MutableStateFlow<FaceAuthState>(FaceAuthState.Idle)
    val state: StateFlow<FaceAuthState> = _state.asStateFlow()

    fun verifyFace(imagePath: String) {
        viewModelScope.launch {
            _state.value = FaceAuthState.Loading
            try {
                val user = userRepository.getCurrentUserSync()
                if (user == null) {
                    _state.value = FaceAuthState.Error("No user logged in.")
                    return@launch
                }

                when (val result = faceAuthService.verifyFromImage(user.id, imagePath)) {
                    is FaceVerificationResult.Match -> _state.value = FaceAuthState.Success
                    is FaceVerificationResult.NoMatch -> _state.value =
                        FaceAuthState.Error("Face not recognized. Please try again.")
                    FaceVerificationResult.NotEnrolled -> _state.value =
                        FaceAuthState.Error("No enrolled face scan. Please enroll your face first.")
                }
            } catch (_: NoFaceDetectedException) {
                _state.value = FaceAuthState.Error("No face detected. Please retake the photo.")
            } catch (_: FaceModelUnavailableException) {
                _state.value = FaceAuthState.Error(
                    "Face model not available. Please ensure a valid face_auth.tflite is present in assets."
                )
            } catch (e: Exception) {
                _state.value = FaceAuthState.Error(e.localizedMessage ?: "Face authentication failed.")
            }
        }
    }

    fun reset() {
        _state.value = FaceAuthState.Idle
    }
}
