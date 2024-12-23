package com.frozenkro.dirtie_client.data.repository

import com.frozenkro.dirtie_client.data.api.DirtieSrvApi
import com.frozenkro.dirtie_client.data.api.models.ApiDevice
import com.frozenkro.dirtie_client.data.api.models.ApiDeviceDataPoint
import java.text.SimpleDateFormat
import java.time.Instant.now
import java.util.Calendar
import java.util.TimeZone

class DeviceRepository(
    private val api: DirtieSrvApi,
    private val userRepository: UserRepository
) {
    suspend fun getDevices(): Result<List<ApiDevice>> {
        return try {
            if (!userRepository.isUserAuthenticated()) {
                return Result.failure(Exception("Not authenticated"))
            }

            val response = api.getDevices()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCapacitance(deviceId: Int, days: Int): Result<List<ApiDeviceDataPoint>> {
        return try {
            if (!userRepository.isUserAuthenticated()) {
                return Result.failure(Exception("Not authenticated"))
            }

            val df = SimpleDateFormat("yyy-MM-dd'T'HH:mm'Z'")
            df.setTimeZone(TimeZone.getTimeZone("UTC"))
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            val start = df.format(calendar.time)

            val response = api.getCapacitance(deviceId, start)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
