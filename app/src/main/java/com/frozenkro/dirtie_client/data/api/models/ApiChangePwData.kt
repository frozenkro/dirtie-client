package com.frozenkro.dirtie_client.data.api.models

data class ApiChangePwData(
    val username: String,
    val success: String,
    val error: Boolean,
    val errorMessage: String,
)
