package com.example.truckmate.ui.screens

import android.content.Intent
import android.location.Location
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.truckmate.data.model.HelpRequest
import com.example.truckmate.data.model.HelpType
import com.example.truckmate.data.model.ObjectType
import com.example.truckmate.service.LocationService
import com.example.truckmate.ui.components.AddHelpDialog
import com.example.truckmate.ui.components.AddObjectDialog
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.utils.LocationHelper
import com.example.truckmate.utils.LocationUtils
import com.example.truckmate.viewmodel.AuthViewModel
import com.example.truckmate.viewmodel.DriverLocationViewModel
import com.example.truckmate.viewmodel.HelpViewModel
import com.example.truckmate.viewmodel.ObjectViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.exp

@Composable
fun MapScreen(viewModel: ObjectViewModel, navController: NavController) {
    val objects by viewModel.objects.collectAsState()
    val selectedObject = viewModel.selectedObject

    val driverViewModel: DriverLocationViewModel = viewModel()
    val drivers by driverViewModel.drivers.collectAsState()

    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var firstLocation by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    val selectedType by viewModel.selectedType.collectAsState()

    val radiusFilter by viewModel.radiusFilter.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val helpViewModel: HelpViewModel = viewModel()
    val helpRequests by helpViewModel.helpRequests.collectAsState()
    var showHelpDialog by remember { mutableStateOf(false) }
    val selectedHelpRequest = helpViewModel.selectedHelpRequest
    val authViewModel: AuthViewModel = viewModel()

    val currentUserId = remember {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    LaunchedEffect(Unit) {
        viewModel.loadIcons(context)
        locationHelper.startLocationUpdates { lat, lon ->
            val location = LatLng(lat, lon)
            userLocation = location
            driverViewModel.updateLocation(
                lat,
                lon,
                username = authViewModel.user.value?.username ?: "Driver"
            )
        }
    }

//    LaunchedEffect(Unit) {
//        while(true) {
//            locationHelper.getCurrentLocation { lat, lon ->
//                driverViewModel.updateLocation(
//                    latitude = lat,
//                    longitude = lon,
//                    username = "Driver"
//                )
//            }
//            delay(10000)
//        }
//    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(userLocation) {
        if(firstLocation && userLocation != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(userLocation!!, 14f)
            )

            firstLocation = false
        }
    }

    val filteredObjects = remember (
        objects,
        selectedType,
        radiusFilter,
        userLocation
    ) {
        objects.filter{ obj ->
            val matchesType = selectedType == null || obj.type == selectedType
            val matchesRadius = if(radiusFilter == null || userLocation == null) {
                true
            }
            else {
//                    val distance = LocationUtils.distanceInMeters(userLocation!!.latitude, userLocation!!.longitude, obj.latitude, obj.longitude)
                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation!!.latitude,
                    userLocation!!.longitude,
                    obj.latitude,
                    obj.longitude,
                    distance
                )
                distance[0] <= (radiusFilter!! * 1000)
            }

            matchesType && matchesRadius
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, onMapClick = { viewModel.selectObject(null) }) {
            userLocation?.let {
                Marker(state = MarkerState(position = it), title = "You")
            }

            filteredObjects.forEach { obj ->
                Marker(
                    state = MarkerState(position = LatLng(obj.latitude, obj.longitude)),
                    title = obj.title,
                    snippet = obj.type.name,
                    icon = viewModel.markerIcons[obj.type] ?: BitmapDescriptorFactory.defaultMarker(),
                    onClick = {
                        viewModel.selectObject(obj)
                        true
                    }
                )
            }

            drivers.forEach { driver ->
                if(driver.userId != currentUserId) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                driver.latitude,
                                driver.longitude
                            )
                        ),
                        title = driver.username,
                        snippet = "Driver nearby"
                    )
                }
            }

            helpRequests.filter{ !it.resolved }
                .forEach { help ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                help.latitude,
                                help.longitude
                            )
                        ),
                        title = help.username,
                        snippet = when(help.type) {
                            HelpType.FLAT_TIRE -> "Flat tire"
                            HelpType.ENGINE_PROBLEM -> "Engine problem"
                            HelpType.FIRE -> "Fire"
                            HelpType.MECHANIC -> "Need mechanic"
                            HelpType.TOWING -> "Need towing"
                            HelpType.PARKING -> "Need parking"
                            HelpType.FUEL -> "Fuel emergency"
                            HelpType.POLICE -> "Police warning"
                            HelpType.ACCIDENT -> "Accident"
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED
                        ),
                        onClick = {
                            helpViewModel.selectHelpRequest(help)
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
        selectedHelpRequest?.let { help ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        help.username,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(help.type.name.replace("_", " "))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(help.description)
                    Spacer(modifier = Modifier.height(12.dp))
                    if(help.acceptedByUserId.isEmpty()) {
                        AppButton("I'm coming") {
                            helpViewModel.acceptHelpRequest(help.id)
                        }
                    }
                    else {
                        Text("Accepted by ${help.acceptedByUsername}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    AppButton("Resolve") {
                        helpViewModel.resolveHelpRequest(help.id)
                    }
                }
            }
        }
        Column {
            // pomeri gde treba
            FloatingActionButton(
                onClick = {
                    userLocation?.let {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(it, 14f)
                            )
                        }
                    }
                },
                modifier = Modifier
                    //.align(Alignment.BottomEnd)
                    .padding(bottom = 180.dp, end = 16.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "My location")
            }

            FloatingActionButton(onClick = { navController.navigate("leaderboard") }, modifier = Modifier
                //.align(Alignment.TopStart)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
            ) {
                Icon(Icons.Default.Star, contentDescription = "Leaderboard")
            }

            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier./*align(Alignment.TopStart).*/padding(16.dp)) {
                OutlinedButton(onClick = { expanded = true }) {
                    when(selectedType?.name) {
                        "PARKING" -> Text("Parking")
                        "GAS_STATION" -> Text("Gas station")
                        "SERVICE" -> Text("Service")
                        "POLICE_PATROL" -> Text("Police patrol")
                        "ROADWORKS" -> Text("Roadworks")
                        "RESTRICTION" -> Text("Restriction")
                        "RESTAURANT" -> Text("Restaurant")
                        "REST_AREA" -> Text("Rest area")
                        else -> Text("Filter")
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = {
                            viewModel.setFilter(null)
                            expanded = false
                        }
                    )
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
                                viewModel.setFilter(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
            var radiusExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedButton(onClick = { radiusExpanded = true }) {
                    Text(if(radiusFilter == null) "Radius: All" else "Radius: ${ radiusFilter?.toInt() } km")
                }
                DropdownMenu(expanded = radiusExpanded, onDismissRequest = { radiusExpanded = false }) {
                    DropdownMenuItem(
                        text = { Text("All") },
                        onClick = { viewModel.setRadiusFilter(null); radiusExpanded = false }
                    )
                    listOf(5f, 10f, 20f, 50f, 100f).forEach { km ->
                        DropdownMenuItem(
                            text = { Text("$km km") },
                            onClick = {
                                viewModel.setRadiusFilter(km)
                                radiusExpanded = false
                            }
                        )
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
        if(selectedObject == null) {
            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FloatingActionButton(onClick = { showDialog = true }, modifier = Modifier
                    .padding(16.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                FloatingActionButton(onClick = {
                    showHelpDialog = true
                }) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Help"
                    )
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
        if(showHelpDialog) {
            AddHelpDialog(
                onDismiss = {
                    showHelpDialog = false
                },
                onSave = { type, description ->
                    locationHelper.getCurrentLocation { lat, lon ->
                        val firebaseUser = FirebaseAuth.getInstance().currentUser
                        val user = authViewModel.user.value
                        val request = HelpRequest(
                            userId = firebaseUser?.uid ?: "",
                            username = user?.username ?: "Driver",
                            userImageUrl = user?.imageUrl ?: "",
                            latitude = lat,
                            longitude = lon,
                            type = type,
                            description = description
                        )

                        helpViewModel.addHelpRequest(request)
                    }
                    showHelpDialog = false
                }
            )
        }
    }

    BackHandler(enabled = selectedObject != null) {
        viewModel.selectObject(null)
    }
}