package com.example.truckmate.data.repository

import androidx.compose.runtime.snapshotFlow
import com.example.truckmate.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getUser(userId: String): User? {
        return db.collection("users").document(userId).get().await().toObject(User::class.java)
    }

    fun getUserRealtime(userId: String, onUpdate: (User?) -> Unit) {
        db.collection("users").document(userId).addSnapshotListener { snapshot, _ ->
            val user = snapshot?.toObject(User::class.java)
            onUpdate(user)
        }
    }
}