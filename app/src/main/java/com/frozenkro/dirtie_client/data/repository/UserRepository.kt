package com.frozenkro.dirtie_client.data.repository

import com.frozenkro.dirtie_client.data.api.DirtieSrvApi
import com.frozenkro.dirtie_client.data.api.models.LoginRequest
import com.frozenkro.dirtie_client.data.api.models.User
// TODO replace auth token pattern with cookie that the api uses

class UserRepository(private val api: DirtieSrvApi) {
    // Store user token for API calls
    private var authToken: String? = null

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                authToken = response.body()!!.token
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = api.forgotPassword(email)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAuthToken(): String? = authToken

    fun logout() {
        authToken = null
    }
}
