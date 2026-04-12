package com.example.truckmate.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.truckmate.ui.screens.LoginScreen
import com.example.truckmate.ui.screens.RegisterScreen
import com.example.truckmate.viewmodel.AuthViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

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
            //HomeScreen()
        }
    }
}