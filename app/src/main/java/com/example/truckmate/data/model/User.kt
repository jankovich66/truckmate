package com.example.truckmate.data.model

data class User(
    val id: String = "",
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val imageUrl: String = "",
    val totalPoints: Int = 0,
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val createdAt: Long = 0
)
