package com.example.truckmate.data.model

data class LocationObject(
    val id: String = "",
    val type: ObjectType = ObjectType.PARKING,
    val title: String = "",
    val description: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val userId: String = ""
)
