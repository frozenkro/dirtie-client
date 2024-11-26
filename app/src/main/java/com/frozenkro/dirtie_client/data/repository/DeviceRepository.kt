package com.frozenkro.dirtie_client.data.repository

import com.frozenkro.dirtie_client.data.api.DirtieSrvApi
import com.frozenkro.dirtie_client.data.api.models.Device

class DeviceRepository(
    private val api: DirtieSrvApi,
    private val userRepository: UserRepository
) {
    suspend fun getDevices(): Result<List<Device>> {
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
}
