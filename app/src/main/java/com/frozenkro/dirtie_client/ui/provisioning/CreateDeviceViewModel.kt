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
) : ViewModel(), NextClickHandler {
    private val deviceName = MutableStateFlow("")

    override fun handleNextClick() {
        createDevice()
    }
    private fun createDevice() = viewModelScope.launch {
        sharedViewModel.uiState.value = DpUiState.Loading
        try {
            val res = provisionRepository.createProvision(deviceName.value)
            res.onSuccess {
                sharedViewModel.uiState.value = DpUiState.Continue
            }
            res.onFailure {
                sharedViewModel.uiState.value = DpUiState.Error(res.exceptionOrNull()?.message ?: "Unknown Error")
            }
        } catch (e: Exception) {
            sharedViewModel.uiState.value = DpUiState.Error(e.message)
        }
    }

    fun onNameChanged(name: String) {
        deviceName.value = name
        if (name.isNotBlank()) {
            sharedViewModel.uiState.value = DpUiState.Ready
        }
    }
}