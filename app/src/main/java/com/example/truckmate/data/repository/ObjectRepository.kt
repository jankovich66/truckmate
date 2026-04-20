package com.example.truckmate.data.repository

import com.example.truckmate.data.model.LocationObject
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ObjectRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("objects");
    private val currentUserId: String? = Firebase.auth.currentUser?.uid;

    suspend fun addObject(record: LocationObject) {
        val docRef = collection.document()

        val newRecord = record.copy(
            id = docRef.id,
            userId = currentUserId ?: ""
        )
        docRef.set(newRecord).await()

        currentUserId?.let {
            increaseUserPoints(it)
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
            val currentPoints = snapshot.getLong("totalPoints") ?: 0
            transaction.update(userRef, "totalPoints", currentPoints + 10)
        }.await()
    }
}