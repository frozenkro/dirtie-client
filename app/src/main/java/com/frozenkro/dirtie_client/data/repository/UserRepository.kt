package com.frozenkro.dirtie_client.data.repository

import com.frozenkro.dirtie_client.data.api.DirtieSrvApi
import com.frozenkro.dirtie_client.data.api.models.ApiLoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val api: DirtieSrvApi) {
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = api.login(ApiLoginRequest(email, password))
            if (response.isSuccessful) {
                _isAuthenticated.value = true
                Result.success(Unit)
            } else {
                val errorMessage = when (response.code()) {
                    401 -> "Invalid Credentials"
                    403 -> "Account Locked"
                    404 -> "Account Not Found"
                    500 -> "Server Error"
                    else -> "Login failed: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }

    suspend fun logout() {
        try {
            api.logout()
        } finally {
            _isAuthenticated.value = false
        }
    }

    // Helper function to check if user is authenticated
    fun isUserAuthenticated() = _isAuthenticated.value

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

}
