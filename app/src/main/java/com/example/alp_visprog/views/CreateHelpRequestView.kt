package com.example.alp_visprog.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import com.example.alp_visprog.viewModel.CreateHelpRequestViewModel

// --- Colors ---
val BrandTeal = Color(0xFF4ECDC4)
val TextDark = Color(0xFF1F2937)
val TextGray = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)

// --- Main Composable ---
@Composable
fun CreateHelpRequestView(
    viewModel: CreateHelpRequestViewModel = viewModel(factory = CreateHelpRequestViewModel.Factory),
    onBackClick: () -> Unit
) {
    val dataStatus by viewModel.dataStatus.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(dataStatus) {
        when (val status = dataStatus) {
            is CreateExchangeUIState.Error -> {
                Toast.makeText(context, status.errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
            is CreateExchangeUIState.Success -> {
                Toast.makeText(context, "Tawaran berhasil dibuat!", Toast.LENGTH_SHORT).show()
                viewModel.resetForm()
                onBackClick()
            }
            else -> {}
        }
    }

    CreateHelpRequestContent(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f) // Bottom Sheet Height
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)), // Rounded Top
        nameOfProduct = viewModel.nameOfProduct,
        onNameChange = { viewModel.nameOfProduct = it },
        location = viewModel.location,
        onLocationChange = { viewModel.location = it },
        categoryIdInput = viewModel.categoryIdInput,
        onCategoryIdChange = { viewModel.categoryIdInput = it },
        exchangeProductName = viewModel.exchangeProductName,
        onExchangeProductChange = { viewModel.exchangeProductName = it },
        imageUrl = viewModel.imageUrl,
        onImageUrlChange = { viewModel.imageUrl = it },
        description = viewModel.description,
        onDescriptionChange = { viewModel.description = it },
        dataStatus = dataStatus,
        onSubmit = { viewModel.submitHelpRequest() },
        onClose = onBackClick
    )
}

// --- Content Composable (Stateless) ---
@Composable
fun CreateHelpRequestContent(
    modifier: Modifier = Modifier,
    nameOfProduct: String,
    onNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    categoryIdInput: String,
    onCategoryIdChange: (String) -> Unit,
    exchangeProductName: String,
    onExchangeProductChange: (String) -> Unit,
    imageUrl: String,
    onImageUrlChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    dataStatus: CreateExchangeUIState,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    // We use Scaffold inside the Box/Column to handle Fixed Header + Fixed Footer + Scrollable Body automatically
    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buat Tawaran Baru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Transparent, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextDark
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = BorderGray)
            }
        },
        bottomBar = {
            // Sticky Bottom Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandTeal,
                        contentColor = Color.White
                    ),
                    enabled = dataStatus !is CreateExchangeUIState.Loading
                ) {
                    if (dataStatus is CreateExchangeUIState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Buat Tawaran", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        // Scrollable Form Content
        Column(
            modifier = Modifier
                .padding(paddingValues) // Respects topBar and bottomBar space
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Kategori Selection
            Column {
                Text("Kategori", fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryButton(
                        text = "Barang",
                        isSelected = categoryIdInput == "1" || categoryIdInput.isEmpty(),
                        onClick = { onCategoryIdChange("1") },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryButton(
                        text = "Jasa",
                        isSelected = categoryIdInput == "2",
                        onClick = { onCategoryIdChange("2") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. Nama Barang
            DesignTextField(
                value = nameOfProduct,
                onValueChange = onNameChange,
                label = "Nama Barang",
                placeholder = "Contoh: Bor Listrik",
                icon = Icons.Outlined.Inventory2
            )

            // 3. Foto Barang
            Column {
                Text("Foto Barang", fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(
                            width = 2.dp,
                            color = BorderGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl.isEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = null,
                                tint = BrandTeal,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tambah URL Foto", color = BrandTeal, fontWeight = FontWeight.Medium)
                        }
                        TextField(
                            value = imageUrl,
                            onValueChange = onImageUrlChange,
                            modifier = Modifier.fillMaxSize().alpha(0f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = onImageUrlChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("URL Foto") },
                            singleLine = true
                        )
                    }
                }
            }

            // 4. Deskripsi
            Column {
                Text("Deskripsi", fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Jelaskan detail barang...", color = TextGray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandTeal,
                        unfocusedBorderColor = BorderGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // 5. Exchange
            DesignTextField(
                value = exchangeProductName,
                onValueChange = onExchangeProductChange,
                label = "Mau ditukar dengan apa?",
                placeholder = "Contoh: Uang / Beras",
                icon = Icons.Outlined.Handshake
            )

            // 6. Lokasi
            DesignTextField(
                value = location,
                onValueChange = onLocationChange,
                label = "Lokasi Anda",
                placeholder = "Contoh: Jakarta Barat",
                icon = Icons.Default.LocationOn
            )

            // Extra space at the bottom to ensure nothing is hidden behind the footer (Scaffold handles this, but good for safety)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- Helper Components ---
@Composable
fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) BrandTeal else Color.White
    val contentColor = if (isSelected) Color.White else TextGray
    val borderColor = if (isSelected) BrandTeal else BorderGray

    Box(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(containerColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, color = contentColor)
    }
}

@Composable
fun DesignTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, fontWeight = FontWeight.Medium, color = TextDark, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextGray) },
            leadingIcon = {
                Icon(imageVector = icon, contentDescription = null, tint = if(value.isNotEmpty()) BrandTeal else TextGray)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandTeal,
                unfocusedBorderColor = BorderGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = BrandTeal
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true
        )
    }
}

fun Modifier.alpha(alpha: Float) = this.then(Modifier.drawWithContent {
    if (alpha > 0) drawContent()
})

// --- Previews ---

// This Preview uses a short height (600dp) to FORCE scrolling so you can test it
@Preview(showBackground = true, heightDp = 600, name = "Short Device (Scrollable)")
@Composable
fun CreateHelpRequestScrollPreview() {
    CreateHelpRequestContent(
        modifier = Modifier.fillMaxSize(),
        nameOfProduct = "",
        onNameChange = {},
        location = "",
        onLocationChange = {},
        categoryIdInput = "1",
        onCategoryIdChange = {},
        exchangeProductName = "",
        onExchangeProductChange = {},
        imageUrl = "",
        onImageUrlChange = {},
        description = "",
        onDescriptionChange = {},
        dataStatus = CreateExchangeUIState.Idle,
        onSubmit = {},
        onClose = {}
    )
}

// This Preview shows the full layout extended
@Preview(showBackground = true, heightDp = 1000, name = "Full Layout (Static)")
@Composable
fun CreateHelpRequestFullPreview() {
    CreateHelpRequestContent(
        modifier = Modifier.fillMaxSize(),
        nameOfProduct = "Bor Listrik",
        onNameChange = {},
        location = "Jakarta",
        onLocationChange = {},
        categoryIdInput = "1",
        onCategoryIdChange = {},
        exchangeProductName = "Uang",
        onExchangeProductChange = {},
        imageUrl = "",
        onImageUrlChange = {},
        description = "Deskripsi...",
        onDescriptionChange = {},
        dataStatus = CreateExchangeUIState.Idle,
        onSubmit = {},
        onClose = {}
    )
}