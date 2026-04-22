package com.example.truckmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.truckmate.ui.components.LeaderboardItem
import com.example.truckmate.viewmodel.LeaderboardViewModel

@Composable
fun LeaderboardScreen(viewModel: LeaderboardViewModel, navController: NavController) {
    val users by viewModel.users.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            itemsIndexed(users) { index, user ->
                LeaderboardItem(index + 1, user)
            }
        }

        FloatingActionButton(onClick = { navController.navigate("map") }, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("\uD83D\uDDFA\uFE0F", style = MaterialTheme.typography.headlineSmall)
        }
    }
}