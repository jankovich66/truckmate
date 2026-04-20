package com.example.truckmate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.truckmate.viewmodel.ObjectViewModel

@Composable
fun ObjectDetailsScreen(objectId: String, viewModel: ObjectViewModel) {
    val obj = viewModel.getObjectById(objectId);

    if(obj === null) {
        Text("Object not found")
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = obj.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Type: ${ obj.type }")
        Text("Description: ${ obj.description }")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Latitude: ${ obj.latitude }")
        Text("Longitude: ${ obj.longitude }")
    }
}