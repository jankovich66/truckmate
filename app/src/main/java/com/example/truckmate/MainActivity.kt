package com.example.truckmate

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.truckmate.navigation.NavGraph
import com.example.truckmate.service.LocationService
import com.example.truckmate.ui.colors.TruckMateTheme
import com.example.truckmate.ui.screens.RegisterScreen
import com.example.truckmate.viewmodel.AuthViewModel
import com.google.android.gms.maps.MapsInitializer
import io.grpc.android.BuildConfig
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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