package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScanDevicesViewModel(
    private val sharedViewModel: DeviceProvisioningSharedViewModel
) : ViewModel(), NextClickHandler {

    override fun handleNextClick() {
        sharedViewModel.uiState.value = DpUiState.Continue
    }
}
