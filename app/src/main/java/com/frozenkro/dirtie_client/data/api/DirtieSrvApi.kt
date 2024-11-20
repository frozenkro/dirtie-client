package com.frozenkro.dirtie_client.data.api

import com.frozenkro.dirtie_client.data.api.models.LoginRequest
import com.frozenkro.dirtie_client.data.api.models.User
import com.frozenkro.dirtie_client.data.api.models.Device
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DirtieSrvApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<User>

    @POST("auth/logout")
    suspend fun logout()

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body email: String): Response<Unit>

    @GET("devices")
    suspend fun getDevices(): Response<List<Device>>

    @POST("devices/provision")
    suspend fun getProvisioningToken(): Response<String>
}