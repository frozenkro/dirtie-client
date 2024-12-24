package com.frozenkro.dirtie_client.data.repository

import com.frozenkro.dirtie_client.data.api.DirtieSrvApi

class ProvisionRepository(
    private val api: DirtieSrvApi,
    private val userRepository: UserRepository,
) {
    suspend fun createProvision(name: String): Result<String> {
        return try {
            if (!userRepository.isUserAuthenticated()) {
                return Result.failure(Exception("Not authenticated"))
            }
            val res = api.getProvisioningToken(name)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!)
            } else {
                Result.failure(Exception(res.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}