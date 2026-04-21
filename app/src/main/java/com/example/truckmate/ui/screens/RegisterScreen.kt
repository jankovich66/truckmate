package com.example.truckmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.truckmate.ui.components.AppButton
import com.example.truckmate.ui.components.AppTextField
import com.example.truckmate.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel, onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(username, { username = it }, "Username")
        AppTextField(fullName, { fullName = it }, "Full name")
        AppTextField(email, { email = it }, "Email")
        AppTextField(phone, { phone = it }, "Phone")
        AppTextField(password, { password = it }, "Password")
        AppButton("Register") {
            viewModel.register(email, password, username, fullName, phone, onSuccess = onRegisterSuccess)
        }
    }
}