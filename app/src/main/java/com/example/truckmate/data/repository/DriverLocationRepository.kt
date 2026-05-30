package com.example.truckmate.data.repository

import androidx.compose.runtime.snapshotFlow
import com.example.truckmate.data.model.DriverLocation
import com.google.firebase.firestore.FirebaseFirestore

class DriverLocationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("drivers_location")

    fun updateLocation(driver: DriverLocation) {
        collection.document(driver.userId).set(driver)
    }

    fun listenForDrivers(onUpdate: (List<DriverLocation>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            if(snapshot != null) {
                val drivers = snapshot.toObjects(DriverLocation::class.java)
                onUpdate(drivers)
            }
        }
    }
}