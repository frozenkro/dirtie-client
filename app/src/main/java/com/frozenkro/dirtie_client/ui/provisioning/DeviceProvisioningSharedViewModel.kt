package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DeviceProvisioningSharedViewModel : ViewModel() {
    val nextEnabled = MutableStateFlow(false)
    val currentStage = MutableStateFlow<ProvisioningStage>(ProvisioningStage.Create)

}

