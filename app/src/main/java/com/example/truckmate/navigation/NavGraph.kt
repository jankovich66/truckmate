package com.example.truckmate.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.truckmate.ui.screens.HomeScreen
import com.example.truckmate.ui.screens.LoginScreen
import com.example.truckmate.ui.screens.ObjectDetailsScreen
import com.example.truckmate.ui.screens.RegisterScreen
import com.example.truckmate.viewmodel.AuthViewModel
import com.example.truckmate.viewmodel.ObjectViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val objectViewModel: ObjectViewModel = viewModel()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(authViewModel) {
                navController.navigate("home")
            }
        }

        composable("register") {
            RegisterScreen(authViewModel) {
                navController.navigate("home")
            }
        }

        composable("home") {
            HomeScreen(objectViewModel, navController)
        }

        composable("details/{objectId}") { backStackEntry ->
            val objectId = backStackEntry.arguments?.getString("objectId") ?: ""
            ObjectDetailsScreen(objectId, objectViewModel)
        }
    }
}