package com.example.truckmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.ui.components.ProfileItem
import com.example.truckmate.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, navController: NavController) {
    val user by authViewModel.user.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        FloatingActionButton(onClick = { authViewModel.logout { navController.navigate("login") { popUpTo(0) { inclusive = true } } } }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            Icon(painter = painterResource(id = com.example.truckmate.R.drawable.logout), contentDescription = "Logout", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(5.dp).size(20.dp))
        }
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if(user == null) {
                CircularProgressIndicator()
                return@Column
            }

            if(user!!.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(user!!.imageUrl),
                    contentDescription = "Profile image",
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            }
            else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "No image",
                    modifier = Modifier.size(120.dp)
                )
            }

            ProfileItem("Username", user!!.username)
            ProfileItem("Full name", user!!.fullName)
            ProfileItem("Email", user!!.email)
            ProfileItem("Phone", user!!.phoneNumber)
            Spacer(modifier = Modifier.height(16.dp))
            ProfileItem("Points", user!!.totalPoints.toString())
            Spacer(modifier = Modifier.height(32.dp))
        }
        FloatingActionButton(onClick = { navController.navigate("map") }, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("\uD83D\uDDFA\uFE0F", style = MaterialTheme.typography.headlineSmall)
        }
    }
}