package com.frozenkro.dirtie_client.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.data.api.models.User
import com.frozenkro.dirtie_client.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = userRepository.login(email, password)
            _loginState.value = when {
                result.isSuccess -> LoginState.Success
                result.isFailure -> LoginState.Error(
                    result.exceptionOrNull()?.message ?: "Unknown error"
                )
                else -> LoginState.Error("Unknown server error")
            }

        }
    }

    sealed class LoginState {
        data object Loading : LoginState()
        data object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}