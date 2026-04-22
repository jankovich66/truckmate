package com.example.truckmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truckmate.data.model.User
import com.example.truckmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        val userId = repository.getCurrentUserId()
        userId?.let { id ->
            viewModelScope.launch {
                _user.value = repository.getUser(id)
            }
            repository.getUserRealtime(id) {
                _user.value = it
            }
        }
    }

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
        _user.value = null
        onLogout()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUser() != null
    }
}
