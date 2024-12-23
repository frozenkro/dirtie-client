package com.frozenkro.dirtie_client.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.domain.devices.DeviceService
import com.frozenkro.dirtie_client.domain.models.Device
import com.frozenkro.dirtie_client.util.DeviceServiceException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceListViewModel(
    private val deviceService: DeviceService,
) : ViewModel() {
    val devices: StateFlow<List<Device>> = deviceService.devices

    init {
        refreshDevices()
    }

    fun refreshDevices() {
        viewModelScope.launch {
            try {
                deviceService.refreshDevices()
            } catch (e: DeviceServiceException) {
                // Yum!
            }
        }
    }
}