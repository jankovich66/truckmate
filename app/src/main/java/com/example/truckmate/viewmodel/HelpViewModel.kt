package com.example.truckmate.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.truckmate.data.model.HelpRequest
import com.example.truckmate.data.repository.HelpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth

class HelpViewModel : ViewModel() {
    private val repository = HelpRepository()
    private val _helpRequests = MutableStateFlow<List<HelpRequest>>(emptyList())
    val helpRequests = _helpRequests.asStateFlow()

    var selectedHelpRequest by mutableStateOf<HelpRequest?>(null)
        private set

    init {
        repository.listenForHelpRequests {
            _helpRequests.value = it
        }
    }

    fun addHelpRequest(helpRequest: HelpRequest) {
        repository.addHelpRequest(helpRequest)
    }

    fun resolveHelpRequest(id: String) {
        repository.resolveHelpRequest(id)
    }

    fun selectHelpRequest(helpRequest: HelpRequest?) {
        selectedHelpRequest = helpRequest
    }

    fun acceptHelpRequest(helpId: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        repository.acceptHelpRequest(helpId, firebaseUser?.uid ?: "", "Driver")
    }
}