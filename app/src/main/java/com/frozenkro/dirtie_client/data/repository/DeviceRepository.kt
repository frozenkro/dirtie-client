package com.frozenkro.dirtie_client.data.repository

import com.frozenkro.dirtie_client.data.api.DirtieSrvApi
import com.frozenkro.dirtie_client.data.api.models.ApiDevice
import com.frozenkro.dirtie_client.data.api.models.ApiDeviceDataPoint
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DeviceRepository(
    private val api: DirtieSrvApi,
    private val userRepository: UserRepository
) {
    private val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

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

    suspend fun getProvisioningToken(): Result<String> {
        return try {
            if (!userRepository.isUserAuthenticated()) {
                return Result.failure(Exception("Not authenticated"))
            }

            val response = api.getProvisioningToken()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.contract)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildStartTime(days: Int): String {
        val start = OffsetDateTime.now(ZoneOffset.UTC).minusDays(days.toLong())
        return start.format(isoFormatter)
    }

    suspend fun getCapacitance(deviceId: Int, days: Int): Result<List<ApiDeviceDataPoint>> {
        return try {
            if (!userRepository.isUserAuthenticated()) {
                return Result.failure(Exception("Not authenticated"))
            }

            val start = buildStartTime(days)
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

    suspend fun getTemperature(deviceId: Int, days: Int): Result<List<ApiDeviceDataPoint>> {
        return try {
            if (!userRepository.isUserAuthenticated()) {
                return Result.failure(Exception("Not authenticated"))
            }

            val start = buildStartTime(days)
            val response = api.getTemperature(deviceId, start)
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
