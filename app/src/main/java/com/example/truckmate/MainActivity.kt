package com.example.truckmate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.truckmate.navigation.NavGraph
import com.example.truckmate.service.LocationService
import com.example.truckmate.ui.colors.TruckMateTheme
import com.google.android.gms.maps.MapsInitializer
import com.example.truckmate.notification.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannels(this)

        MapsInitializer.initialize(applicationContext)

        setContent {
            val context = LocalContext.current

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true

                if(fineLocationGranted) {
                    val intent = Intent(context, LocationService::class.java)
                    ContextCompat.startForegroundService(context, intent)
                }
            }

            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }

            TruckMateTheme {
                NavGraph()
            }
        }

    }
}