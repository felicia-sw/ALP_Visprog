package com.example.alp_visprog.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import com.example.alp_visprog.viewModel.CreateExchangeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExchangeView(
    helpRequestId: Int,
    viewModel: CreateExchangeViewModel = viewModel(factory = CreateExchangeViewModel.Factory),
    onBackClick: () -> Unit
) {
    // 1. Collect the StateFlow
    val dataStatus by viewModel.dataStatus.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 2.LaunchedEffect for Side Effects
    LaunchedEffect(dataStatus) {
        when (val status = dataStatus) {
            is CreateExchangeUIState.Error -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage() // Reset so toast doesn't show again on rotate
            }
            is CreateExchangeUIState.Success -> {
                Toast.makeText(context, "Offer sent successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetForm()
                onBackClick() // Navigate back automatically
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Offer Help") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "I can help with this!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // FORM FIELDS
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = { viewModel.phone = it },
                label = { Text("Phone Number (WhatsApp)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Message (How can you help?)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            // SUBMIT BUTTON
            Button(
                onClick = { viewModel.submitOffer(helpRequestId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = dataStatus !is CreateExchangeUIState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C35))
            ) {
                if (dataStatus is CreateExchangeUIState.Loading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Submit Offer", fontSize = 16.sp)
                }
            }
        }
    }
}