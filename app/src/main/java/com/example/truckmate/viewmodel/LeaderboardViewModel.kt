package com.example.truckmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.truckmate.data.model.User
import com.example.truckmate.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LeaderboardViewModel: ViewModel() {
    private val repository = UserRepository()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        repository.getUsersRealtime { usersList ->
            _users.value = usersList.sortedByDescending { it.totalPoints }
        }
    }
}