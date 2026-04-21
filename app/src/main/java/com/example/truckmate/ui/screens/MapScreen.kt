package com.example.truckmate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.truckmate.ui.components.AddObjectDialog
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.utils.LocationHelper
import com.example.truckmate.viewmodel.ObjectViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition

@Composable
fun MapScreen(viewModel: ObjectViewModel, navController: NavController) {
    val objects by viewModel.objects.collectAsState()
    val selectedObject = viewModel.selectedObject

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        locationHelper.getCurrentLocation { lat, lon ->
            userLocation = LatLng(lat, lon)
        }
    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 14f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState) {
            userLocation?.let {
                Marker(state = MarkerState(position = it), title = "You")
            }

            objects.forEach { obj ->
                Marker(
                    state = MarkerState(position = LatLng(obj.latitude, obj.longitude)),
                    title = obj.title,
                    snippet = obj.type.name,
                    onClick = {
                        viewModel.selectObject(obj)
                        true
                    }
                )
            }
        }
        selectedObject?.let { obj ->
            Card(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(obj.title, style = MaterialTheme.typography.titleMedium)
                    Text(obj.type.name)
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton("View details") {
                        navController.navigate("details/${ obj.id }")
                    }
                }
            }
        }
        FloatingActionButton(onClick = { showDialog = true }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
            Text("+")
        }

        FloatingActionButton(onClick = { navController.navigate("list") }, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("☰")
        }
        if(showDialog) {
            AddObjectDialog(onDismiss = { showDialog = false }, onSave = { title, description, type ->
                locationHelper.getCurrentLocation { lat, lon ->
                    viewModel.addObject(title, description, type, lat, lon)
                }
                showDialog = false
            })
        }
    }
}