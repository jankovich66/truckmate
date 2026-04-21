package com.example.truckmate.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.truckmate.data.model.ObjectType
import androidx.compose.runtime.*

@Composable
fun AddObjectDialog(onDismiss: () -> Unit, onSave: (String, String, ObjectType) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ObjectType.PARKING) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSave(title, description, selectedType)
            }) {
                Text("Save", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.primary)
            }
        },
        title = { Text("Add object", color = MaterialTheme.colorScheme.primary) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        when(selectedType.name) {
                            "PARKING" -> Text("Parking")
                            "GAS_STATION" -> Text("Gas station")
                            "SERVICE" -> Text("Service")
                            "POLICE_PATROL" -> Text("Police patrol")
                            "ROADWORKS" -> Text("Roadworks")
                            "RESTRICTION" -> Text("Restriction")
                            "RESTAURANT" -> Text("Restaurant")
                            "REST_AREA" -> Text("Rest area")
                        }
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        ObjectType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    when(type.name) {
                                        "PARKING" -> Text("Parking")
                                        "GAS_STATION" -> Text("Gas station")
                                        "SERVICE" -> Text("Service")
                                        "POLICE_PATROL" -> Text("Police patrol")
                                        "ROADWORKS" -> Text("Roadworks")
                                        "RESTRICTION" -> Text("Restriction")
                                        "RESTAURANT" -> Text("Restaurant")
                                        "REST_AREA" -> Text("Rest area")
                                    }
                                },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}