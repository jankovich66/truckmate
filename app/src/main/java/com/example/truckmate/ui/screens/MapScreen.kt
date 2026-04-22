package com.example.truckmate.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.truckmate.data.model.ObjectType
import com.example.truckmate.ui.components.AddObjectDialog
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.utils.LocationHelper
import com.example.truckmate.viewmodel.ObjectViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.material.floatingactionbutton.FloatingActionButton

@Composable
fun MapScreen(viewModel: ObjectViewModel, navController: NavController) {
    val objects by viewModel.objects.collectAsState()
    val selectedObject = viewModel.selectedObject

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadIcons(context)
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
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, onMapClick = { viewModel.selectObject(null) }) {
            userLocation?.let {
                Marker(state = MarkerState(position = it), title = "You")
            }

            objects.forEach { obj ->
                Marker(
                    state = MarkerState(position = LatLng(obj.latitude, obj.longitude)),
                    title = obj.title,
                    snippet = obj.type.name,
//                    icon = getMarkerIcon(obj.type),
                    icon = viewModel.markerIcons[obj.type] ?: BitmapDescriptorFactory.defaultMarker(),
                    onClick = {
                        viewModel.selectObject(obj)
                        true
                    }
                )
            }
        }
        selectedObject?.let { obj ->
            Card(modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(obj.title, style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { viewModel.selectObject(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
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
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton("View details") {
                        navController.navigate("details/${ obj.id }")
                    }
                }
            }
        }
        FloatingActionButton(onClick = { navController.navigate("profile") }, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile")
        }
        if(selectedObject === null) {
            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(onClick = { showDialog = true }, modifier = Modifier
                    .padding(16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }

                FloatingActionButton(onClick = { navController.navigate("list") }, modifier = Modifier
                    .padding(16.dp)) {
                    Icon(Icons.AutoMirrored.Default.List, contentDescription = "List")
                }
            }
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

    BackHandler(enabled = selectedObject != null) {
        viewModel.selectObject(null)
    }
}