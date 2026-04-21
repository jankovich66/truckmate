package com.example.truckmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.truckmate.data.model.ObjectType
import com.example.truckmate.ui.colors.TextSecondary
import com.example.truckmate.viewmodel.ObjectViewModel

@Composable
fun ObjectDetailsScreen(objectId: String, viewModel: ObjectViewModel) {
    val obj = viewModel.getObjectById(objectId);

    if(obj === null) {
        Text("Object not found")
        return
    }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text(text = obj.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        when(obj.type) {
            ObjectType.PARKING -> Text("Parking")
            ObjectType.POLICE_PATROL -> Text("Police patrol")
            ObjectType.GAS_STATION -> Text("Gas station")
            ObjectType.RESTAURANT -> Text("Restaurant")
            ObjectType.RESTRICTION -> Text("Restriction")
            ObjectType.REST_AREA -> Text("Rest area")
            ObjectType.ROADWORKS -> Text("Roadworks")
            ObjectType.SERVICE -> Text("Service")
        }
        Text("Description: ${obj.description}")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Latitude: ${obj.latitude}")
        Text("Longitude: ${obj.longitude}")
    }
}