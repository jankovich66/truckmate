package com.example.truckmate.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.truckmate.ui.components.AddObjectDialog
import com.example.truckmate.viewmodel.ObjectViewModel

@Composable
fun HomeScreen(viewModel: ObjectViewModel) {
    val objects by viewModel.objects.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(objects) { obj ->
                Column(modifier = Modifier.padding(8.dp).clickable {

                }) {
                    Text(obj.title)
                    Text(obj.type.name)
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+")
        }

        if(showDialog) {
            AddObjectDialog(
                onDismiss = { showDialog = false },
                onSave = { title, description, type ->
                    viewModel.addObject(title, description, type, latitude = 43.32, longitude = 21.89)
                    showDialog = false
                }
            )
        }
    }
}