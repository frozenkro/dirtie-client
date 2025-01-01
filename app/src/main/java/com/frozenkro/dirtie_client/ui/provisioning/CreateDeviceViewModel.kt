package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.data.repository.ProvisionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateDeviceViewModel(
    private val provisionRepository: ProvisionRepository,
    private val sharedViewModel: DeviceProvisioningSharedViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow<DpUiState>(DpUiState.Initial)
    val uiState = _uiState.asStateFlow()
    private val deviceName = MutableStateFlow("")

    fun createDevice() = viewModelScope.launch {
        _uiState.value = DpUiState.Loading
        try {
            val res = provisionRepository.createProvision(deviceName.value)
            res.onSuccess {
                _uiState.value = DpUiState.Continue
            }
            res.onFailure {
                _uiState.value = DpUiState.Error(res.exceptionOrNull()?.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            _uiState.value = DpUiState.Error(e.message)
        }
    }

    fun onNameChanged(name: String) {
        deviceName.value = name
        if (name.isNotBlank()) {
            _uiState.value = DpUiState.Ready
        }
    }
}