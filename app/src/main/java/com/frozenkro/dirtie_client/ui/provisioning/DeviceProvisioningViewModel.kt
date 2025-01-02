package com.frozenkro.dirtie_client.ui.provisioning

import androidx.lifecycle.ViewModel
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
                createDeviceViewModel
            }

            R.id.scanDevicesFragment -> {
                scanDevicesViewModel
            }

            R.id.deviceCredentialFragment -> {
                deviceCredentialViewModel
            }

            else -> {
                null
            }
        }?.handleNextClick()
    }

    fun complete() {
        sharedViewModel.complete()
    }
}