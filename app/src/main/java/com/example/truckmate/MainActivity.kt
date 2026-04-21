package com.example.truckmate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.truckmate.navigation.NavGraph
import com.example.truckmate.ui.colors.TruckMateTheme
import com.example.truckmate.ui.screens.RegisterScreen
import com.example.truckmate.viewmodel.AuthViewModel
import io.grpc.android.BuildConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TruckMateTheme {
                NavGraph()
            }
        }
    }
}