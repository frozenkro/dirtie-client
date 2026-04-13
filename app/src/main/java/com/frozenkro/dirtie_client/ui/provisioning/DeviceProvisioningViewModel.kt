package com.frozenkro.dirtie_client.ui.provisioning

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.data.repository.DeviceRepository
import com.frozenkro.dirtie_client.domain.wifi.WifiProvisioningManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

class DeviceProvisioningViewModel(
    private val deviceRepository: DeviceRepository,
    private val wifiManager: WifiProvisioningManager,
    private val context: Context
) : ViewModel() {

    sealed class ProvisioningState {
        object Idle : ProvisioningState()
        object Loading : ProvisioningState()
        object Success : ProvisioningState()
        data class Error(val message: String) : ProvisioningState()
    }

    private val _state = MutableStateFlow<ProvisioningState>(ProvisioningState.Idle)
    val state: StateFlow<ProvisioningState> = _state.asStateFlow()

    fun provisionDevice(ssid: String, pass: String) {
        viewModelScope.launch {
            _state.value = ProvisioningState.Loading
            
            // 1. Get provisioning token from server
            val tokenResult = deviceRepository.getProvisioningToken()
            if (tokenResult.isFailure) {
                _state.value = ProvisioningState.Error("Failed to get token: ${tokenResult.exceptionOrNull()?.message}")
                return@launch
            }
            val token = tokenResult.getOrNull() ?: ""

            // 2. Connect to Device AP (assuming a known fixed SSID for setup)
            // In a real app, we might scan for "Dirtie-XXXX"
            val connected = wifiManager.connectToDeviceAP("Dirtie-Setup") 
            if (!connected) {
                _state.value = ProvisioningState.Error("Could not connect to device AP. Is the device in setup mode?")
                return@launch
            }

            // 3. Send provisioning data
            val success = wifiManager.sendProvisioningData(
                ssid = ssid,
                password = pass,
                provisioningToken = token
            )

            if (success) {
                _state.value = ProvisioningState.Success
            } else {
                _state.value = ProvisioningState.Error("Failed to send config to device. Please try again.")
            }
        }
    }
}
