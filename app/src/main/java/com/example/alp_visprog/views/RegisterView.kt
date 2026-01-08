package com.example.alp_visprog.views

import android.widget.Toast
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.alp_visprog.R
import com.example.alp_visprog.uiStates.AuthenticationStatusUIState
import com.example.alp_visprog.viewModel.AuthenticationViewModel
import kotlinx.coroutines.delay

@Composable
fun RegisterView(
    navController: NavHostController?,
    authenticationViewModel: AuthenticationViewModel = viewModel(factory = AuthenticationViewModel.Factory)
) {
    val context = LocalContext.current
    val authStatus = authenticationViewModel.authenticationStatus

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showLocationPicker by remember { mutableStateOf(false) }
    var firstOpen by remember { mutableStateOf(true) }

    LaunchedEffect(firstOpen) {
        if (firstOpen) {
            firstOpen = false
            if (authenticationViewModel.locationNameInput.isBlank()) {
                delay(150)
                showLocationPicker = true
            }
        }
    }

    LaunchedEffect(username) {
        authenticationViewModel.changeUsernameInput(username)
        authenticationViewModel.checkRegisterForm()
    }
    LaunchedEffect(email) {
        authenticationViewModel.changeEmailInput(email)
        authenticationViewModel.checkRegisterForm()
    }
    LaunchedEffect(password) {
        authenticationViewModel.changePasswordInput(password)
        authenticationViewModel.checkRegisterForm()
    }
    LaunchedEffect(confirmPassword) {
        authenticationViewModel.changeConfirmPasswordInput(confirmPassword)
        authenticationViewModel.checkRegisterForm()
    }

    LaunchedEffect(authStatus) {
        when (authStatus) {
            is AuthenticationStatusUIState.Failed -> {
                showError = true
                errorMessage = authStatus.errorMessage
            }
            is AuthenticationStatusUIState.Success -> {
                showError = false
                Toast.makeText(context, "Registrasi berhasil! Mengalihkan...", Toast.LENGTH_LONG).show()
            }
            else -> {
                showError = false
            }
        }
    }

    // Brand colors
    val orangeColor = Color(0xFFF9794D)
    val peachColor = Color(0xFFFFB399)
    val tealColor = Color(0xFF4ECDC4)

    // Location Picker Dialog
    if (showLocationPicker) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showLocationPicker = false }
        ) {
            LocationPickerView(
                onLocationSelected = { name, lat, lon ->
                    authenticationViewModel.locationNameInput = name
                    authenticationViewModel.latitudeInput = lat
                    authenticationViewModel.longitudeInput = lon
                    showLocationPicker = false
                },
                onClose = { showLocationPicker = false }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            orangeColor,
                            peachColor.copy(alpha = 0.85f),
                            tealColor.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Pattern overlay
        Image(
            painter = painterResource(id = R.drawable.pattern_tukerin),
            contentDescription = "Background Pattern",
            modifier = Modifier.fillMaxSize().alpha(0.12f),
            contentScale = ContentScale.Crop
        )

        // FIXED: Main scrollable content - scroll wraps EVERYTHING
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding(), // Add navigation bar padding for bottom
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section - Made smaller for Register to give more form space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(160.dp), // Reduced height
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Logo - smaller for register
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_4),
                            contentDescription = "Tuker.In Logo",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Text(
                        text = "Tuker.In",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Buat Akun Baru",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f)
                    )
                }
            }

            // FIXED: Content Card - using wrapContentHeight instead of weight
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // KEY FIX: Allow card to grow with content
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glassmorphic Tab Selector
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                        ) {
                            // Inactive Tab - Masuk
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clickable { navController?.navigate("login") },
                                shape = RoundedCornerShape(16.dp),
                                color = Color.Transparent
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text("Masuk", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            // Active Tab - Daftar
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = orangeColor,
                                shadowElevation = 4.dp
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text("Daftar", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Message
                    if (showError) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFEBEE).copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, Color(0xFFEF5350).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Warning, "Error", tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                                Text(text = errorMessage, color = Color(0xFFD32F2F), fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Form Fields - reduced spacing
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Username
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Username", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF333333))
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                placeholder = { Text("Pilih username unik", fontSize = 13.sp, color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = orangeColor, modifier = Modifier.size(20.dp)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = orangeColor,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                enabled = authStatus !is AuthenticationStatusUIState.Loading,
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                        }

                        // Location
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Lokasi Rumah", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF333333))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .clickable { showLocationPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                color = if (authenticationViewModel.locationNameInput.isNotEmpty()) Color(0xFFFAFAFA) else Color.White,
                                border = BorderStroke(
                                    1.dp,
                                    if (authenticationViewModel.locationNameInput.isNotEmpty()) orangeColor else Color(0xFFE0E0E0)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        null,
                                        tint = if (authenticationViewModel.locationNameInput.isNotEmpty()) orangeColor else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (authenticationViewModel.locationNameInput.isEmpty())
                                            "Klik untuk memilih lokasi"
                                        else
                                            authenticationViewModel.locationNameInput.take(35) + if (authenticationViewModel.locationNameInput.length > 35) "..." else "",
                                        fontSize = 13.sp,
                                        color = if (authenticationViewModel.locationNameInput.isEmpty()) Color.Gray else Color.Black,
                                        maxLines = 1
                                    )
                                }
                            }
                            if (authenticationViewModel.locationNameInput.isNotEmpty()) {
                                Text("✓ Lokasi terpilih", fontSize = 11.sp, color = Color(0xFF4CAF50), modifier = Modifier.padding(start = 4.dp))
                            }
                        }

                        // Email
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Email", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF333333))
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                placeholder = { Text("nama@email.com", fontSize = 13.sp, color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = orangeColor, modifier = Modifier.size(20.dp)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = orangeColor,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                enabled = authStatus !is AuthenticationStatusUIState.Loading,
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                        }

                        // Password
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Kata Sandi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF333333))
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                placeholder = { Text("Minimal 8 karakter", fontSize = 13.sp, color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Lock, null, tint = orangeColor, modifier = Modifier.size(20.dp)) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(40.dp)) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = "Toggle",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
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
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                enabled = authStatus !is AuthenticationStatusUIState.Loading,
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                        }

                        // Confirm Password
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Konfirmasi Kata Sandi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF333333))
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                placeholder = { Text("Ketik ulang kata sandi", fontSize = 13.sp, color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Default.Lock, null, tint = orangeColor, modifier = Modifier.size(20.dp)) },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }, modifier = Modifier.size(40.dp)) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = "Toggle",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = orangeColor,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                ),
                                enabled = authStatus !is AuthenticationStatusUIState.Loading,
                                singleLine = true,
                                isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                            )
                            if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text("Kata sandi tidak cocok", color = Color(0xFFD32F2F), fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Register Button
                    Button(
                        onClick = { navController?.let { authenticationViewModel.register(it) } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = orangeColor,
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        ),
                        enabled = username.isNotEmpty() &&
                                email.isNotEmpty() &&
                                password.isNotEmpty() &&
                                confirmPassword.isNotEmpty() &&
                                password == confirmPassword &&
                                authenticationViewModel.locationNameInput.isNotEmpty() &&
                                authStatus !is AuthenticationStatusUIState.Loading
                    ) {
                        if (authStatus is AuthenticationStatusUIState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Daftar Akun", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Login Prompt
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sudah punya akun?", color = Color.Gray, fontSize = 13.sp)
                        TextButton(onClick = { navController?.navigate("login") }) {
                            Text("Masuk", color = orangeColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Extra padding at bottom for safe area
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}