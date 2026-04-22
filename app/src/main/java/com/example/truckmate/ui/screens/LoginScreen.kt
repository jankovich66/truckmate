package com.example.truckmate.ui.screens

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.ui.components.AppTextField
import com.example.truckmate.viewmodel.AuthViewModel
import androidx.compose.material3.Icon

@Composable
fun LoginScreen(viewModel: AuthViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("Login", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(email, { email = it }, "Email")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if(passwordVisible)
                    painterResource(id = android.R.drawable.ic_menu_view)
                else
                    painterResource(id = android.R.drawable.ic_menu_close_clear_cancel)

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = image, contentDescription = "Toggle password")
                }
            }
        )
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 8.dp))
        }

        AppButton("Login") {
            val error = viewModel.validateLogin(email, password)

            if(error != null) {
                errorMessage = error
            }
            else {
                errorMessage = null
                viewModel.login(email, password,
                    onSuccess = {
                        navController.navigate("map") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    },
                    onError = { fireError ->
                        errorMessage = fireError
                    }
                )
            }
        }

        Row {
            Text("Don't have account? ")
            Text("Register", modifier = Modifier.clickable{ navController.navigate("register") }, color = MaterialTheme.colorScheme.primary)
        }
    }
}