package com.example.truckmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.truckmate.data.model.User
import com.example.truckmate.data.repository.AuthRepository

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    fun register(email: String, password: String, username: String, fullName: String, phone: String, onSuccess: () -> Unit) {
        val user = User(
            email = email,
            username = username,
            fullName = fullName,
            phoneNumber = phone,
            createdAt = System.currentTimeMillis()
        )

        repository.register(email, password, user, onSuccess)
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        repository.login(email, password, onSuccess)
    }

    fun logout(onLogout: () -> Unit) {
        repository.logout()
        onLogout()
    }
}
