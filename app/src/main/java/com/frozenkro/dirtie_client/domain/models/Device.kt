package com.frozenkro.dirtie_client.domain.models

data class Device (
    val id: Int,
    val name: String,
    val currentCapacitance: Double,
    val historicalCapacitance: List<Reading>,
    val currentTemperature: Double = 0.0,
    val historicalTemperature: List<Reading> = emptyList(),
)