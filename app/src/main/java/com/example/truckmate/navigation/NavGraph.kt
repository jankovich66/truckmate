package com.example.truckmate.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.truckmate.service.LocationService
import com.example.truckmate.ui.screens.LeaderboardScreen
import com.example.truckmate.ui.screens.LoginScreen
import com.example.truckmate.ui.screens.MapScreen
import com.example.truckmate.ui.screens.ObjectDetailsScreen
import com.example.truckmate.ui.screens.ObjectListScreen
import com.example.truckmate.ui.screens.ProfileScreen
import com.example.truckmate.ui.screens.RegisterScreen
import com.example.truckmate.viewmodel.AuthViewModel
import com.example.truckmate.viewmodel.LeaderboardViewModel
import com.example.truckmate.viewmodel.ObjectViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val objectViewModel: ObjectViewModel = viewModel()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if(authViewModel.isUserLoggedIn()) {
            val intent = Intent(context, LocationService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    val startDestination = if(authViewModel.isUserLoggedIn()) "map" else "login"

    NavHost(navController, startDestination = startDestination) {
        composable("login") {
            val context = LocalContext.current
            LoginScreen(authViewModel, navController) {
                ContextCompat.startForegroundService(context, Intent(context, LocationService::class.java))
            }
        }

        composable("register") {
            val context = LocalContext.current
            RegisterScreen(authViewModel, navController) {
                ContextCompat.startForegroundService(context, Intent(context, LocationService::class.java))
            }
        }

        composable("list") {
            ObjectListScreen(objectViewModel, navController)
        }

        composable("details/{objectId}") { backStackEntry ->
            val objectId = backStackEntry.arguments?.getString("objectId") ?: ""
            ObjectDetailsScreen(objectId, objectViewModel)
        }

        composable("map") {
            MapScreen(objectViewModel, navController)
        }

        composable("profile") {
            ProfileScreen(authViewModel, navController)
        }

        composable("leaderboard") {
            val vm: LeaderboardViewModel = viewModel()
            LeaderboardScreen(vm, navController)
        }
    }
}