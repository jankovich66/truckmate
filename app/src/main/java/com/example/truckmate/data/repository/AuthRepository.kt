package com.example.truckmate.data.repository

import com.example.truckmate.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun register(email: String, password: String, user: User, onSuccess: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = auth.currentUser?.uid ?: ""
            val newUser = user.copy(id = userId)

            db.collection("users").document(userId).set(newUser).addOnSuccessListener { onSuccess() }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            onSuccess()
        }
    }
}