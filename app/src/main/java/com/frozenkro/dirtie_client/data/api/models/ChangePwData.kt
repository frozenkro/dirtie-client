package com.frozenkro.dirtie_client.data.api.models

data class ChangePwData(
    val username: String,
    val success: String,
    val error: Boolean,
    val errorMessage: String,
)
