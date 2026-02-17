package com.biocube.app.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biocube.app.domain.model.User
import com.biocube.app.domain.repository.IUserRepository
import com.biocube.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            userRepository.login(username, password).collect { resource ->
                _loginState.value = when (resource) {
                    is Resource.Loading -> LoginState.Loading
                    is Resource.Success -> {
                        resource.data?.let { LoginState.Success(it) } ?: LoginState.Error("User data is null")
                    }
                    is Resource.Error -> LoginState.Error(resource.message ?: "Login failed")
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
