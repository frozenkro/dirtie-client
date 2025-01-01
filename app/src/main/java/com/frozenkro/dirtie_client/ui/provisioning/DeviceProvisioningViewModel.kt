package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.frozenkro.dirtie_client.R

class DeviceProvisioningViewModel(
    private val createDeviceViewModel: CreateDeviceViewModel,
    private val sharedViewModel: DeviceProvisioningSharedViewModel,
    private val scanDevicesViewModel: ScanDevicesViewModel,
    private val deviceCredentialViewModel: DeviceCredentialViewModel
) : ViewModel() {
    fun handleNextClicked(destId: Int?) {
        when (destId) {
            R.id.createDeviceFragment -> {
                createDeviceViewModel.createDevice()
            }
            R.id.scanDevicesFragment -> {
                scanDevicesViewModel.uiState.value = DpUiState.Continue
            }
        }
    }
}