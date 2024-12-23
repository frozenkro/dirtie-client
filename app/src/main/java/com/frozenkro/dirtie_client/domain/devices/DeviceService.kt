package com.frozenkro.dirtie_client.domain.devices

import com.frozenkro.dirtie_client.data.api.models.ApiDeviceDataPoint
import com.frozenkro.dirtie_client.data.repository.DeviceRepository
import com.frozenkro.dirtie_client.domain.models.Device
import com.frozenkro.dirtie_client.domain.models.Reading
import com.frozenkro.dirtie_client.util.CoroutineDispatchers
import com.frozenkro.dirtie_client.util.DeviceServiceException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.text.DateFormat

class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val dispatchers: CoroutineDispatchers
) {
    private val deviceCache = MutableStateFlow<Map<Int, Device>>(emptyMap())

    val devices: StateFlow<List<Device>> = deviceCache
        .map { it.values.toList() }
        .stateIn(
            scope = CoroutineScope(dispatchers.io + SupervisorJob()),
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    suspend fun refreshDevices() {
        withContext(dispatchers.io) {
            try {
                val deviceReq = deviceRepository.getDevices()
                deviceReq.onSuccess { devices ->
                    val capacitance = devices.map { device ->
                        async {
                            deviceRepository.getCapacitance(device.deviceId, 30)
                        }
                    }.awaitAll()

                    val deviceMap = devices.zip(capacitance) { device, readings ->
                        val sortedReadings = readings.getOrDefault(emptyList<ApiDeviceDataPoint>())
                            .map { it.toReading() }
                            .sortedByDescending { it.timestamp }

                        Device(
                            id = device.deviceId,
                            name = device.displayName,
                            currentCapacitance = sortedReadings.firstOrNull()?.value ?: 0.0,
                            historicalCapacitance = sortedReadings
                        )
                    }.associateBy { it.id }

                    deviceCache.update { deviceMap }
                }
            } catch (e: Exception) {
                throw DeviceServiceException("Failed to refresh devices", e)
            }
        }
    }

    fun getDevice(deviceId: Int): Flow<Device?> = deviceCache
        .map { it[deviceId] }

    fun getDeviceReadings(deviceId: Int): Flow<List<Reading>> = deviceCache
        .map { it[deviceId]?.historicalCapacitance ?: emptyList() }

    private fun ApiDeviceDataPoint.toReading(): Reading {
        val df = DateFormat.getDateInstance()
        val timestamp = df.parse(time)?.toInstant() ?: throw Exception()
        return Reading(
            timestamp = timestamp.epochSecond,
            value = value.toDouble()
        )
    }
}
