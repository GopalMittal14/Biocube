package com.biocube.splash.presentation

import androidx.lifecycle.ViewModel
import com.biocube.core.data.faceauth.FaceAuthService
import com.biocube.core.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val faceAuthService: FaceAuthService
) : ViewModel() {

    suspend fun getCurrentUserSync() = userRepository.getCurrentUserSync()

    suspend fun hasEnrolledFace(userId: String): Boolean = faceAuthService.hasEnrollment(userId)

    suspend fun logout() = userRepository.logout()
}
