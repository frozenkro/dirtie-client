package com.frozenkro.dirtie_client.data.api

import com.frozenkro.dirtie_client.data.api.models.ApiCreateUserArgs
import com.frozenkro.dirtie_client.data.api.models.ApiLoginRequest
import com.frozenkro.dirtie_client.data.api.models.ApiUser
import com.frozenkro.dirtie_client.data.api.models.ApiDevice
import com.frozenkro.dirtie_client.data.api.models.ApiDeviceDataPoint
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DirtieSrvApi {
    @POST("login")
    suspend fun login(@Body loginRequest: ApiLoginRequest): Response<Unit>

    @POST("logout")
    suspend fun logout(): Response<Unit>

    @POST("users")
    suspend fun createUser(@Body args: ApiCreateUserArgs): Response<ApiUser>

    @POST("pw/reset")
    suspend fun forgotPassword(@Body email: String): Response<Unit>

    @GET("devices")
    suspend fun getDevices(): Response<List<ApiDevice>>

    @POST("devices/createProvision")
    suspend fun getProvisioningToken(@Query("displayName") displayName: String): Response<String>

    @GET("data/capacitance")
    suspend fun getCapacitance(@Query("deviceId") deviceId: Int, @Query("startTime") startTime: String): Response<List<ApiDeviceDataPoint>>

    @GET("data/temperature")
    suspend fun getTemperature(@Query("deviceId") deviceId: Int, @Query("startTime") startTime: String): Response<List<ApiDeviceDataPoint>>
}