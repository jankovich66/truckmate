package com.example.truckmate.data.model

data class DriverLocation (
    val userId: String = "",
    val username: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
)