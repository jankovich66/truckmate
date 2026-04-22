package com.example.truckmate.data.repository

import com.example.truckmate.data.model.LocationObject
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class ObjectRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("objects");
    private val currentUserId: String?
        get() = Firebase.auth.currentUser?.uid;

    suspend fun addObject(record: LocationObject) {
        val docRef = collection.document()

        val userId = currentUserId ?: ""

        val newRecord = record.copy(
            id = docRef.id,
            userId = userId
        )
        docRef.set(newRecord).await()

        if(userId.isNotEmpty()) {
            increaseUserPoints(userId)
        }
    }

    fun getObjectsRealtime(onUpdate: (List<LocationObject>) -> Unit) {
        collection.addSnapshotListener { snapshot, _ ->
            if(snapshot != null) {
                val objects = snapshot.toObjects(LocationObject::class.java)
                onUpdate(objects)
            }
        }
    }

    private suspend fun increaseUserPoints(userId: String?) {
        val userRef = db.collection("users").document(userId!!)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            if(!snapshot.exists()) {
                throw FirebaseFirestoreException("User document not found",
                    FirebaseFirestoreException.Code.NOT_FOUND)
            }
            val currentPoints = snapshot.getLong("totalPoints") ?: 0
            transaction.update(userRef, "totalPoints", currentPoints + 10)
        }.await()
    }

    suspend fun getNearbyObjects(userLat: Double, userLon: Double, radiusMeters: Double): List<LocationObject> {
        val snapshot = collection.get().await()
        val objects = snapshot.toObjects(LocationObject::class.java)

        return objects.filter { obj ->
            val distance = FloatArray(1)

            android.location.Location.distanceBetween(userLat, userLon, obj.latitude, obj.longitude, distance)

            distance[0] <= radiusMeters
        }
    }
}