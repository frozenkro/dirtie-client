package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DeviceProvisioningSharedViewModel : ViewModel() {
    val uiState = MutableStateFlow<DpUiState>(DpUiState.Initial)
    val currentStage = MutableStateFlow<ProvisioningStage>(ProvisioningStage.Create)
    private val _completed = MutableSharedFlow<Unit>()
    val completed = _completed.asSharedFlow()
    val networkFound = MutableStateFlow(false)

    fun complete() {
        viewModelScope.launch{
            _completed.emit(Unit)
        }
    }
}