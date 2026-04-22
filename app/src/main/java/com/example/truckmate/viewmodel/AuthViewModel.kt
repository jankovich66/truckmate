package com.example.truckmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.truckmate.data.model.User
import com.example.truckmate.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if(uid != null) {
                fetchUserData(uid)
            }
            else {
                _user.value = null
            }
        }
    }

    fun register(email: String, password: String, username: String, fullName: String, phone: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = User(
            email = email,
            username = username,
            fullName = fullName,
            phoneNumber = phone,
            createdAt = System.currentTimeMillis()
        )

        repository.register(email, password, user,
            onSuccess = {
                val userId = repository.getCurrentUserId()
                userId?.let { fetchUserData(it) }
                onSuccess()
            },
            onError = { errorMessage ->
                onError(errorMessage)
            }
        )
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.login(email, password,
            onSuccess = {
                onSuccess()
            },
            onError = { errorMessage ->
                onError(errorMessage)
            }
        )
    }

    fun logout(onLogout: () -> Unit) {
        repository.logout()
        _user.value = null
        onLogout()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUser() != null
    }

    fun fetchUserData(userId: String) {
        viewModelScope.launch {
            _user.value = repository.getUser(userId)
        }
        repository.getUserRealtime(userId) { updatedUser ->
            _user.value = updatedUser
        }
    }

    fun validateLogin(email: String, password: String): String? {
        if(email.isBlank() || password.isBlank())
            return "All fields are required"
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Email address is not valid"
        return null
    }

    fun validateRegister(email: String, password: String, username: String, fullName: String, phone: String): String? {
        if(email.isBlank() || password.isBlank() || username.isBlank() || fullName.isBlank() || phone.isBlank())
            return "All fields are required"
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return "Email address is not valid"
        if(password.length < 6)
            return "Password musr be at least 6 characters long"
        if(phone.length < 9)
            return "Phone number is too short"
        return null
    }
}

