package com.example.truckmate.data.repository

import com.example.truckmate.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getUsersRealtime(onUpdate: (List<User>) -> Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, _ ->
                if(snapshot != null) {
                    val users = snapshot.toObjects(User::class.java)
                    onUpdate(users)
                }
            }
    }
}