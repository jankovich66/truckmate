package com.example.truckmate.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.snapshotFlow
import com.example.truckmate.data.model.User
import com.example.truckmate.service.LocationService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun register(email: String, password: String, user: User, imageUri: Uri?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid ?: ""

                if(imageUri != null) {
                    val ref = storage.reference.child("profile_images/$userId.jpg")
                    ref.putFile(imageUri).continueWithTask {
                        ref.downloadUrl
                    }.addOnSuccessListener { uri ->
                        val newUser = user.copy(
                            id = userId,
                            imageUrl = uri.toString()
                        )

                        db.collection("users").document(userId).set(newUser).addOnSuccessListener {
                            onSuccess()
                        }
                    }
                }
                else {
                    val newUser = user.copy(id = userId)
                    db.collection("users").document(userId).set(newUser).addOnSuccessListener { onSuccess() }
                }
            }
            .addOnFailureListener { exception ->
                val message = when(exception) {
                    is FirebaseAuthUserCollisionException -> "Email already exists"
                    is FirebaseAuthWeakPasswordException -> "Password is too weak"
                    else -> "Something went wrong"
                }
                onError(message)
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                val message = when(exception) {
                    is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                    is FirebaseAuthInvalidUserException -> "User not found"
                    else -> "Something went wrong"
                }
                onError(message)
            }
    }

    fun logout(context: Context) {
        auth.signOut()
        context.stopService(Intent(context, LocationService::class.java))
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