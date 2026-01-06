package com.example.alp_visprog.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.alp_visprog.uiStates.AuthenticationStatusUIState
import com.example.alp_visprog.viewModel.AuthenticationViewModel

@Composable
fun LoginView(
    navController: NavHostController?,
    authenticationViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    val authStatus = authenticationViewModel.authenticationStatus

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(email) {
        authenticationViewModel.changeEmailInput(email)
        authenticationViewModel.checkLoginForm()
    }

    LaunchedEffect(password) {
        authenticationViewModel.changePasswordInput(password)
        authenticationViewModel.checkLoginForm()
    }

    LaunchedEffect(authStatus) {
        when (authStatus) {
            is AuthenticationStatusUIState.Failed -> {
                showError = true
                errorMessage = authStatus.errorMessage
            }
            is AuthenticationStatusUIState.Success -> {
                showError = false
            }
            else -> {
                showError = false
            }
        }
    }

    val orangeColor = Color(0xFFF9794D)
    val backgroundColor = Color(0xFFFFF6E3)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(orangeColor)
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Handshake,
                            contentDescription = "Tuker.In Logo",
                            modifier = Modifier.size(60.dp),
                            tint = orangeColor
                        )
                    }
                    Text(
                        text = "Tuker.In",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Platform Barter Komunitas",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tab Selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = orangeColor
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                "Masuk",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clickable { navController?.navigate("register") },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                "Daftar",
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Error Message
            if (showError) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = errorMessage,
                            color = Color(0xFFD32F2F),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Form Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Email Field
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Email",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "nama@email.com",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = orangeColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color(0xFFF5F5F5)
                        ),
                        enabled = authStatus !is AuthenticationStatusUIState.Loading,
                        singleLine = true
                    )
                }

                // Password Field
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Kata Sandi",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Masukkan kata sandi",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = orangeColor
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color(0xFFF5F5F5)
                        ),
                        enabled = authStatus !is AuthenticationStatusUIState.Loading,
                        singleLine = true
                    )
                }

                // Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* TODO: Navigate to Forgot Password */ }) {
                        Text(
                            "Lupa kata sandi?",
                            color = orangeColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Login Button
            Button(
                onClick = {
                    navController?.let { authenticationViewModel.login(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = orangeColor,
                    disabledContainerColor = Color(0xFFE0E0E0)
                ),
                enabled = email.isNotEmpty() &&
                        password.isNotEmpty() &&
                        authStatus !is AuthenticationStatusUIState.Loading
            ) {
                if (authStatus is AuthenticationStatusUIState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        "Masuk",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Belum punya akun?",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(onClick = { navController?.navigate("register") }) {
                    Text(
                        "Daftar",
                        color = orangeColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}