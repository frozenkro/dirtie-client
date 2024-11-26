package com.frozenkro.dirtie_client.data.api.models

data class DeviceDataPoint(
    val value: Int,
    val time: String, // Eventually should guarantee ISO8601 from server and use type LocalDateTime
    val key: String,
)
