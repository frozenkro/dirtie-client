package com.frozenkro.dirtie_client.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableLiveData<RegisterState>()
    val state: LiveData<RegisterState> = _state

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = RegisterState.Loading
            val result = userRepository.register(name, email, password)
            _state.value = if (result.isSuccess) {
                RegisterState.Success
            } else {
                RegisterState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class RegisterState {
        data object Loading : RegisterState()
        data object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}
