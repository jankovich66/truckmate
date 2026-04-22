package com.example.truckmate.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.truckmate.ui.screens.LoginScreen
import com.example.truckmate.ui.screens.MapScreen
import com.example.truckmate.ui.screens.ObjectDetailsScreen
import com.example.truckmate.ui.screens.ObjectListScreen
import com.example.truckmate.ui.screens.ProfileScreen
import com.example.truckmate.ui.screens.RegisterScreen
import com.example.truckmate.viewmodel.AuthViewModel
import com.example.truckmate.viewmodel.ObjectViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val objectViewModel: ObjectViewModel = viewModel()

    val startDestination = if(authViewModel.isUserLoggedIn()) "map" else "login"

    NavHost(navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(authViewModel, navController) {
                navController.navigate("map") {
                    popUpTo("login") {
                        inclusive = true
                    }
                }
            }
        }

        composable("register") {
            RegisterScreen(authViewModel) {
                navController.navigate("map") {
                    popUpTo("register") {
                        inclusive = true
                    }
                }
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
    }
}