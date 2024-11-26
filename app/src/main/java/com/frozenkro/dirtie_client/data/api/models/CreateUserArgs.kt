package com.frozenkro.dirtie_client.data.api.models

data class CreateUserArgs(
    val email: String,
    val password: String,
    val name: String,
)
