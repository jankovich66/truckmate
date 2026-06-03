package com.example.truckmate.data.model

data class HelpRequest(
    val id: String = "",

    val userId: String = "",
    val username: String = "",
    val userImageUrl: String = "",

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    val type: HelpType = HelpType.FLAT_TIRE,
    val description: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val resolved: Boolean = false,

    val acceptedByUserId: String = "",
    val acceptedByUsername: String = ""
)
