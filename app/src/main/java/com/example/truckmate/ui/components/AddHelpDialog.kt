package com.example.truckmate.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import com.example.truckmate.data.model.HelpType

@Composable
fun AddHelpDialog(onDismiss: () -> Unit, onSave: (HelpType, String) -> Unit) {
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(HelpType.FLAT_TIRE) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(selectedType, description)
                }
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text("Need help")
        },
        text = {
            Column {
                Box {
                    OutlinedButton(
                        onClick = {
                            expanded = true
                        }
                    ) {
                        Text(
                            when(selectedType) {
                                HelpType.FLAT_TIRE -> "Flat tire"
                                HelpType.ENGINE_PROBLEM -> "Engine problem"
                                HelpType.FIRE -> "Fire"
                                HelpType.MECHANIC -> "Mechanic"
                                HelpType.TOWING -> "Towing"
                                HelpType.PARKING -> "Parking"
                                HelpType.FUEL -> "Fuel"
                                HelpType.POLICE -> "Police"
                                HelpType.ACCIDENT -> "Accident"
                            }
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        HelpType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(type.name.replace("_", " "))
                                },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                /*
                DropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) { }
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Description")
                    }
                )*/
            }
        }
    )
}