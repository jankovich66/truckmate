package com.example.truckmate.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.truckmate.ui.components.AddObjectDialog
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.ui.components.ObjectCard
import com.example.truckmate.utils.LocationHelper
import com.example.truckmate.viewmodel.ObjectViewModel

@Composable
fun ObjectListScreen(viewModel: ObjectViewModel, navController: NavController) {
    val objects by viewModel.objects.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            items(objects) { obj ->
                ObjectCard(obj) {
                    navController.navigate("details/${ obj.id }")
                }
            }
        }

        FloatingActionButton(onClick = { navController.navigate("map") }, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
            Text("\uD83D\uDDFA\uFE0F", style = MaterialTheme.typography.headlineSmall)
        }
    }
}