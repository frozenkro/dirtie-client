package com.frozenkro.dirtie_client.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.frozenkro.dirtie_client.domain.devices.DeviceService
import com.frozenkro.dirtie_client.domain.models.Reading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DeviceChartViewModel(
    private val deviceService: DeviceService
) : ViewModel() {

    sealed class ChartState {
        object Loading : ChartState()
        data class Success(
            val capacitanceReadings: List<Reading>,
            val temperatureReadings: List<Reading>
        ) : ChartState()
        data class Error(val message: String) : ChartState()
    }

    private val _state = MutableStateFlow<ChartState>(ChartState.Loading)
    val state: StateFlow<ChartState> = _state.asStateFlow()

    fun loadDeviceData(deviceId: Int) {
        viewModelScope.launch {
            _state.value = ChartState.Loading
            try {
                // Collect both reading types
                var capReadings = emptyList<Reading>()
                var tempReadings = emptyList<Reading>()

                capReadings = deviceService.getDeviceReadings(deviceId).first()
                tempReadings = deviceService.getDeviceTemperatureReadings(deviceId).first()

                _state.value = ChartState.Success(capReadings, tempReadings)
            } catch (e: Exception) {
                _state.value = ChartState.Error("Failed to load chart data: ${e.message}")
            }
        }
    }
}
