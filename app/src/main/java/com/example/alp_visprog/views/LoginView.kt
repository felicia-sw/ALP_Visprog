package com.example.alp_visprog.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun LoginView(navController: NavController?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val orangeColor = Color(0xFFF9794D)
    val backgroundColor = Color(0xFFFFF6E3)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(orangeColor)
                        .padding(bottom = 30.dp, top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Handshake,
                            contentDescription = "UnityGrid Logo",
                            modifier = Modifier.size(50.dp),
                            tint = orangeColor
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "UnityGrid",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Platform Barter Komunitas",
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            item {
                // Login/Register Toggle
                Card(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 30.dp, end = 30.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { /* Already on Login */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                        ) {
                            Text("Masuk", color = Color.White, fontSize = 15.sp)
                        }
                        Button(
                            onClick = { navController?.navigate("register") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Daftar", color = Color.Black, fontSize = 15.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            // Form Section
            item {
                Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                    Text("Email", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("nama@email.com", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color.LightGray,
                        )
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(15.dp)) }

            item {
                Column(modifier = Modifier.padding(horizontal = 30.dp)) {
                    Text("Kata Sandi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Minimal 6 karakter", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                val imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                Icon(imageVector = imageVector, contentDescription = "Toggle password visibility")
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeColor,
                            unfocusedBorderColor = Color.LightGray,
                        )
                    )
                }
            }

            item {
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* TODO: Navigate to Forgot Password */ }) {
                        Text("Lupa kata sandi?", color = orangeColor, fontSize = 13.sp)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(15.dp)) }

            item {
                Button(
                    onClick = { /* TODO: Handle login */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor)
                ) {
                    Text("Masuk", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Belum punya akun?", color = Color.Gray, fontSize = 13.sp)
                    TextButton(onClick = { navController?.navigate("register") }) {
                        Text("Daftar", color = orangeColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
             item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginViewPreview() {
    LoginView(navController = null)
}