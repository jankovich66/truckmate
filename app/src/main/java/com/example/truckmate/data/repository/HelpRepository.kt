package com.example.truckmate.data.repository

import com.example.truckmate.data.model.HelpRequest
import com.google.firebase.firestore.FirebaseFirestore

class HelpRepository {
    private val db = FirebaseFirestore.getInstance()

    fun addHelpRequest(helpRequest: HelpRequest) {
        val docRef = db.collection("help_requests").document()
        val requestWithId = helpRequest.copy(id = docRef.id)
        docRef.set(requestWithId)
    }

    fun listenForHelpRequests(onDataChanged: (List<HelpRequest>) -> Unit) {
        db.collection("help_requests").addSnapshotListener { snapshot,  _ ->
            if(snapshot != null) {
                val currentTime = System.currentTimeMillis()

                val request = snapshot.documents.mapNotNull {
                        it.toObject(HelpRequest::class.java)
                    }
                    .filter {
                        currentTime - it.createdAt < 2 * 60 * 60 * 1000
                    }

                onDataChanged(request)
            }
        }
    }

    fun resolveHelpRequest(id: String) {
        db.collection("help_requests").document(id).update("resolved", true)
    }

    fun acceptHelpRequest(helpId: String, userId: String, username: String) {
        db.collection("help_requests")
            .document(helpId)
            .update(
                mapOf(
                    "acceptedByUserId" to userId,
                    "acceptedByUsername" to username
                )
            )
    }
}