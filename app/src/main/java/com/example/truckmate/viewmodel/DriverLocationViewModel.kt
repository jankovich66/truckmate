package com.example.truckmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.truckmate.data.model.DriverLocation
import com.example.truckmate.data.repository.DriverLocationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DriverLocationViewModel : ViewModel() {
    private val repository = DriverLocationRepository()
    private val _drivers = MutableStateFlow<List<DriverLocation>>(emptyList())
    val drivers: StateFlow<List<DriverLocation>> = _drivers

    init {
        repository.listenForDrivers {
            _drivers.value = it.filter { driver ->
                System.currentTimeMillis() - driver.timestamp < 30000
            }
        }
    }

    fun updateLocation(latitude: Double, longitude: Double, username: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val driver = DriverLocation(
            userId = userId,
            username = username,
            latitude = latitude,
            longitude = longitude,
            timestamp = System.currentTimeMillis()
        )
        repository.updateLocation(driver)
    }
}