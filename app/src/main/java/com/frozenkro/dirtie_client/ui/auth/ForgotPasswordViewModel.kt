package com.frozenkro.dirtie_client.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.data.repository.UserRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableLiveData<ForgotPasswordState>()
    val state: LiveData<ForgotPasswordState> = _state

    fun submit(email: String) {
        viewModelScope.launch {
            _state.value = ForgotPasswordState.Loading
            val result = userRepository.forgotPassword(email)
            _state.value = if (result.isSuccess) {
                ForgotPasswordState.Success
            } else {
                ForgotPasswordState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class ForgotPasswordState {
        data object Loading : ForgotPasswordState()
        data object Success : ForgotPasswordState()
        data class Error(val message: String) : ForgotPasswordState()
    }
}
