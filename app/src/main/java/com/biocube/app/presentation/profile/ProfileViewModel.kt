package com.biocube.app.presentation.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biocube.core.domain.model.User
import com.biocube.core.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { user ->
                _profileState.value = if (user != null) {
                    ProfileState.Success(user)
                } else {
                    ProfileState.Error("User not found")
                }
            }
        }
    }

    fun saveProfile(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                _profileState.value = ProfileState.Saved
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(e.localizedMessage ?: "Failed to save profile")
            }
        }
    }
}

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    object Saved : ProfileState()
    data class Error(val message: String) : ProfileState()
}
