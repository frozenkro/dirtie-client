package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanDevicesViewModel(
    private val deviceProvisioningSharedViewModel: DeviceProvisioningSharedViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow<DpUiState>(DpUiState.Initial)
    val uiState = _uiState.asStateFlow()
}