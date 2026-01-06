package com.example.alp_visprog.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_visprog.R
import com.example.alp_visprog.ui.theme.BrandOrange
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import com.example.alp_visprog.viewModel.CreateExchangeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExchangeView(
    helpRequestId: Int,
    viewModel: CreateExchangeViewModel = viewModel(factory = CreateExchangeViewModel.Factory),
    onBackClick: () -> Unit
) {
    val dataStatus by viewModel.dataStatus.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Handle Success/Error Toasts
    LaunchedEffect(dataStatus) {
        when (val status = dataStatus) {
            is CreateExchangeUIState.Error -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
            is CreateExchangeUIState.Success -> {
                Toast.makeText(context, "Offer sent successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetForm()
                onBackClick() // Auto-navigate back on success
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Offer Help",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                // Navigation Icon removed here as requested
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandOrange //
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Background pattern
            AsyncImage(
                model = R.drawable.pattern_tukerin,
                contentDescription = "Background Pattern",
                modifier = Modifier.fillMaxSize().alpha(0.3f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "I can help with this!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Please provide your contact details so the requester can reach you.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- Form Fields ---

                // Name Field
                CustomExchangeTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = "Your Name",
                    icon = Icons.Default.Person,
                    placeholder = "Enter your full name"
                )

                // Phone Field
                CustomExchangeTextField(
                    value = viewModel.phone,
                    onValueChange = { viewModel.phone = it },
                    label = "Phone Number (WhatsApp)",
                    icon = Icons.Default.Phone,
                    placeholder = "e.g., 08123456789",
                    keyboardType = KeyboardType.Phone
                )

                // Email Field
                CustomExchangeTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = "Email",
                    icon = Icons.Default.Email,
                    placeholder = "name@email.com",
                    keyboardType = KeyboardType.Email
                )

                // Description Field (Taller)
                CustomExchangeTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = "Message",
                    icon = Icons.Default.Edit,
                    placeholder = "How can you help?",
                    singleLine = false,
                    minLines = 4,
                    modifier = Modifier.height(140.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = { viewModel.submitOffer(helpRequestId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = dataStatus !is CreateExchangeUIState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                ) {
                    if (dataStatus is CreateExchangeUIState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            text = "Submit Offer",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Reusable Text Field Component
@Composable
fun CustomExchangeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BrandOrange
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}