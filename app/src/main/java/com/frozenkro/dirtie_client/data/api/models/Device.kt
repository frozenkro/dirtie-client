package com.frozenkro.dirtie_client.data.api.models

data class Device(
    val deviceId: Int,
    val userId: Int,
    val macAddr: String,
    val displayName: String,
)
