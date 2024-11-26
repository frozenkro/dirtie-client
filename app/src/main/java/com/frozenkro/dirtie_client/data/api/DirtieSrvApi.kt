package com.frozenkro.dirtie_client.data.api

import com.frozenkro.dirtie_client.data.api.models.CreateUserArgs
import com.frozenkro.dirtie_client.data.api.models.LoginRequest
import com.frozenkro.dirtie_client.data.api.models.User
import com.frozenkro.dirtie_client.data.api.models.Device
import com.frozenkro.dirtie_client.data.api.models.DeviceDataPoint
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DirtieSrvApi {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Unit>

    @POST("logout")
    suspend fun logout(): Response<Unit>

    @POST("users")
    suspend fun createUser(@Body args: CreateUserArgs): Response<User>

    @POST("pw/reset")
    suspend fun forgotPassword(@Body email: String): Response<Unit>

    @GET("devices")
    suspend fun getDevices(): Response<List<Device>>

    @POST("devices/createProvision")
    suspend fun getProvisioningToken(): Response<String>

    @GET("data/capacitance")
    suspend fun getCapacitance(@Query("deviceId") deviceId: Int, @Query("startTime") startTime: String): Response<List<DeviceDataPoint>>

    @GET("data/temperature")
    suspend fun getTemperature(@Query("deviceId") deviceId: Int, @Query("startTime") startTime: String): Response<List<DeviceDataPoint>>
}