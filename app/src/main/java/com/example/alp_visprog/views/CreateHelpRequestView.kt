package com.example.alp_visprog.views

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_visprog.uiStates.CreateExchangeUIState
import com.example.alp_visprog.viewModel.CreateHelpRequestViewModel

// --- Colors ---
val BrandCoral = Color(0xFFE97856) // Primary Orange
val BrandCyan = Color(0xFF20C4D4)  // Teal/Cyan
val BackgroundCream = Color(0xFFFCF8F3) // Warm Cream
val TextDark = Color(0xFF1F2121)
val TextLight = Color(0xFFABABAB)
val BorderGray = Color(0xFFE8E8E8)
val IconGray = Color(0xFF999999)

@Composable
fun CreateHelpRequestView(
    viewModel: CreateHelpRequestViewModel = viewModel(factory = CreateHelpRequestViewModel.Factory),
    onBackClick: () -> Unit
) {
    val dataStatus by viewModel.dataStatus.collectAsState()
    val context = LocalContext.current
    var showLocationPicker by remember { mutableStateOf(false) }

    // Image Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.selectedImageUri = uri }
    )

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

    // Location Picker Dialog
    if (showLocationPicker) {
        LocationPickerView(
            onLocationSelected = { name, lat, lon ->
                viewModel.location = name
                viewModel.latitude = lat
                viewModel.longitude = lon
                showLocationPicker = false
            },
            onClose = { showLocationPicker = false }
        )
    }

    CreateHelpRequestContent(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White),

        viewModel = viewModel,
        onImageClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        onSubmit = { viewModel.submitHelpRequest() },
        onClose = onBackClick,
        onLocationClick = { showLocationPicker = true }
    )
}

@Composable
fun CreateHelpRequestContent(
    modifier: Modifier = Modifier,
    viewModel: CreateHelpRequestViewModel,
    onImageClick: () -> Unit,
    onSubmit: () -> Unit,
    onClose: () -> Unit,
    onLocationClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Buat Tawaran Baru", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextDark)
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = BorderGray)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoral, contentColor = Color.White),
                    enabled = viewModel.dataStatus.collectAsState().value !is CreateExchangeUIState.Loading
                ) {
                    Text("Buat Tawaran", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // --- Section 1: Kategori ---
            Text("Kategori", fontWeight = FontWeight.Bold, color = TextDark)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CategoryToggle(
                    text = "Barang",
                    icon = Icons.Outlined.Inventory2,
                    isSelected = viewModel.categoryIdInput == "1",
                    onClick = { viewModel.categoryIdInput = "1" }
                )
                CategoryToggle(
                    text = "Jasa",
                    icon = Icons.Outlined.Build,
                    isSelected = viewModel.categoryIdInput == "2",
                    onClick = { viewModel.categoryIdInput = "2" }
                )
            }

            // --- Section 2: Foto ---
            SectionLabel("Foto")
            PhotoUploadBox(
                uri = viewModel.selectedImageUri,
                onClick = onImageClick
            )

            // --- Section 3: Nama Barang/Jasa ---
            SectionLabel("Nama Barang/Jasa")
            StyledTextField(
                value = viewModel.nameOfProduct,
                onValueChange = { viewModel.nameOfProduct = it },
                placeholder = "Contoh: Sepatu Converse Ukuran 42"
            )

            // --- Section 4: Deskripsi ---
            SectionLabel("Deskripsi")
            StyledTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                placeholder = "Jelaskan kondisi dan detail barang atau jasa...",
                singleLine = false,
                minLines = 4
            )

            // --- Section 5: Mau ditukar dengan apa? ---
            SectionLabel("Mau ditukar dengan apa?")
            StyledTextField(
                value = viewModel.exchangeProductName,
                onValueChange = { viewModel.exchangeProductName = it },
                placeholder = "Contoh: Mencari sepatu olahraga Nike...",
                singleLine = false,
                minLines = 3,
                borderColor = BrandCyan // Teal Border
            )

            // --- Section 6: Lokasi ---
            SectionLabel("Lokasi")
            OutlinedTextField(
                value = viewModel.location.ifEmpty { "" },
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLocationClick() },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextDark,
                    disabledBorderColor = if (viewModel.location.isNotEmpty()) BrandCoral else BorderGray,
                    disabledPlaceholderColor = TextLight,
                    disabledContainerColor = Color.White,
                    disabledLeadingIconColor = if (viewModel.location.isNotEmpty()) BrandCoral else TextLight
                ),
                placeholder = { Text("Klik untuk memilih lokasi", color = TextLight) },
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, null)
                },
                trailingIcon = {
                    if (viewModel.location.isNotEmpty()) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                    }
                },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            if (viewModel.location.isNotEmpty()) {
                Text(
                    "✓ Lokasi terpilih",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // --- Section 7: Info Kontak ---
            Spacer(modifier = Modifier.height(8.dp))
            ContactInfoCard(viewModel)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// --- Helper Components ---

@Composable
fun SectionLabel(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun CategoryToggle(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) BrandCoral else Color.Transparent
    val contentColor = if (isSelected) Color.White else TextDark
    val border = if (isSelected) null else BorderStroke(1.dp, BorderGray)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = border,
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = contentColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PhotoUploadBox(uri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF9FAFB))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CameraAlt, null, tint = TextLight, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tambah Foto", color = TextLight, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    borderColor: Color = BorderGray,
    icon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextLight) },
        shape = RoundedCornerShape(8.dp),
        minLines = minLines,
        singleLine = singleLine,
        leadingIcon = if (icon != null) { { Icon(icon, null, tint = TextLight) } } else null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = TextDark
        )
    )
}

@Composable
fun ContactInfoCard(viewModel: CreateHelpRequestViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCream),
        border = BorderStroke(2.dp, BrandCoral)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = BrandCoral)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Info Kontak", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandCoral)
            }

            // Toggles
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Inactive Button
                OutlinedButton(
                    onClick = { viewModel.loadProfileData() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Icon(Icons.Default.AccountCircle, null, tint = IconGray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gunakan Info Profil", color = IconGray)
                }

                // Active Button
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEFE9)),
                    border = BorderStroke(2.dp, BrandCoral),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = BrandCoral)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Info Kontak", color = BrandCoral)
                }
            }

            // Fields
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ContactField(viewModel.contactName, { viewModel.contactName = it }, "Nama Kontak", "Misal: Pak Budi", null)
                ContactField(viewModel.contactPhone, { viewModel.contactPhone = it }, "Nomor WhatsApp", "08xx xxxx xxxx", Icons.Default.Phone, BrandCyan, KeyboardType.Phone)
                ContactField(viewModel.contactEmail, { viewModel.contactEmail = it }, "Email (Opsional)", "email@contoh.com", Icons.Default.Email, IconGray, KeyboardType.Email)
            }
        }
    }
}

@Composable
fun ContactField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector?,
    iconColor: Color = TextDark,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = TextLight, fontSize = 14.sp) },
            leadingIcon = if (icon != null) { { Icon(icon, null, tint = iconColor) } } else null,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = if(icon == Icons.Default.Phone) BrandCyan else BorderGray,
                unfocusedBorderColor = BorderGray
            ),
            singleLine = true
        )
    }
}