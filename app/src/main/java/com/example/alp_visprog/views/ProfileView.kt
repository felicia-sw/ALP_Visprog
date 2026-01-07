package com.example.alp_visprog.views

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.alp_visprog.R
import com.example.alp_visprog.models.HelpRequestModel
import com.example.alp_visprog.ui.theme.BrandOrange
import com.example.alp_visprog.ui.theme.BrandTeal
import com.example.alp_visprog.uiStates.ProfileStatusUIState
import com.example.alp_visprog.viewModel.ProfileViewModel

// Brand Colors
private val OrangeGradientStart = Color(0xFFF9794D)
private val OrangeGradientEnd = Color(0xFFFFB399)
private val TealAccent = Color(0xFF4ECDC4)
private val CreamBackground = Color(0xFFFFFBF7)

@Composable
fun ProfileView(navController: NavController? = null) {
    val vm: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
    var showEdit by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        Log.d("ProfileView", "🚀 ProfileView launched, fetching profile...")
        try {
            vm.fetchProfile()
        } catch (e: Exception) {
            Log.e("ProfileView", "❌ Error in LaunchedEffect", e)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                Log.d("ProfileView", "🔄 ProfileView resumed - refreshing user data...")
                vm.refreshUserData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            OrangeGradientStart.copy(alpha = 0.15f),
                            OrangeGradientEnd.copy(alpha = 0.1f),
                            TealAccent.copy(alpha = 0.05f),
                            CreamBackground
                        ),
                        startY = 0f,
                        endY = 2000f
                    )
                )
        )

        // Supergraphic Pattern Overlay
        Image(
            painter = painterResource(id = R.drawable.pattern_tukerin),
            contentDescription = "Background Pattern",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.08f),
            contentScale = ContentScale.Crop
        )

        when (val state = vm.profileStatus) {
            is ProfileStatusUIState.Start, is ProfileStatusUIState.Loading -> {
                LoadingState()
            }

            is ProfileStatusUIState.Failed -> {
                ErrorState(
                    message = state.message,
                    onRetry = { vm.fetchProfile() }
                )
            }

            is ProfileStatusUIState.Success -> {
                val profile = state.profile
                if (profile == null) {
                    ErrorState(
                        message = "Data profil tidak tersedia",
                        onRetry = { vm.fetchProfile() }
                    )
                } else {
                    ProfileContent(
                        profile = profile,
                        vm = vm,
                        selectedTab = selectedTab,
                        onTabChange = { selectedTab = it },
                        onEditClick = { showEdit = true },
                        onSettingsClick = { showSettings = true },
                        navController = navController
                    )

                    if (showEdit) {
                        EditProfileDialog(
                            profile = profile,
                            onDismiss = { showEdit = false },
                            onSave = { fullName, location, latitude, longitude, bio ->
                                vm.updateProfile(fullName, location, latitude, longitude, bio)
                                showEdit = false
                            }
                        )
                    }

                    if (showSettings) {
                        SettingsModal(
                            onDismiss = { showSettings = false },
                            onLogout = {
                                vm.logout()
                                showSettings = false
                                navController?.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = OrangeGradientStart,
                strokeWidth = 4.dp,
                modifier = Modifier.size(56.dp)
            )
            Text(
                "Memuat profil...",
                color = Color.Gray,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Error",
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Gagal Memuat Profil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = message,
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeGradientStart
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Coba Lagi", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: com.example.alp_visprog.models.ProfileModel,
    vm: ProfileViewModel,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    navController: NavController?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Enhanced Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                // Gradient Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    OrangeGradientStart,
                                    OrangeGradientEnd
                                )
                            )
                        )
                )

                // Supergraphic Pattern
                Image(
                    painter = painterResource(id = R.drawable.pattern_white),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.15f),
                    contentScale = ContentScale.Crop
                )

                // Profile Image with Glass Effect
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 40.dp)
                ) {
                    // Glass background
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    )

                    // Profile Image
                    if (!profile.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = profile.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(130.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(4.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Profile",
                                modifier = Modifier.size(65.dp),
                                tint = OrangeGradientStart
                            )
                        }
                    }
                }
            }
        }

        item {
            // Profile Info Card with Enhanced Shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-40).dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = profile.fullName ?: profile.username ?: "Pengguna",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = TealAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = profile.location ?: "Belum ada lokasi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats Row with Gradient Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GradientStatCard(
                            number = vm.totalTawaran.toString(),
                            label = "Tawaran",
                            gradientColors = listOf(
                                OrangeGradientStart.copy(alpha = 0.2f),
                                OrangeGradientEnd.copy(alpha = 0.15f)
                            ),
                            iconColor = OrangeGradientStart
                        )
                        GradientStatCard(
                            number = vm.totalBertukar.toString(),
                            label = "Bertukar",
                            gradientColors = listOf(
                                TealAccent.copy(alpha = 0.2f),
                                TealAccent.copy(alpha = 0.1f)
                            ),
                            iconColor = TealAccent
                        )
                        GradientStatCard(
                            number = vm.totalProses.toString(),
                            label = "Proses",
                            gradientColors = listOf(
                                Color(0xFFFF9800).copy(alpha = 0.2f),
                                Color(0xFFFFB74D).copy(alpha = 0.15f)
                            ),
                            iconColor = Color(0xFFFF9800)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons with Gradient
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                OrangeGradientStart,
                                                OrangeGradientEnd
                                            )
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Edit Profil",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Surface(
                            onClick = onSettingsClick,
                            modifier = Modifier.size(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Transparent,
                            border = BorderStroke(2.dp, OrangeGradientStart.copy(alpha = 0.3f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(OrangeGradientStart.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    modifier = Modifier.size(26.dp),
                                    tint = OrangeGradientStart
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Enhanced Tabs
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(4.dp)
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            OrangeGradientStart,
                                            OrangeGradientEnd
                                        )
                                    )
                                )
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { onTabChange(0) },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (selectedTab == 0) OrangeGradientStart else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Aktif",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) Color(0xFF1A1A1A) else Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                    }
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { onTabChange(1) },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (selectedTab == 1) TealAccent else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Berhasil",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) Color(0xFF1A1A1A) else Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            val itemsToDisplay = if (selectedTab == 0) {
                vm.userHelpRequests.filter { !it.isCheckout }
            } else {
                vm.userHelpRequests.filter { it.isCheckout }
            }

            if (vm.isLoadingHelpRequests) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = OrangeGradientStart,
                        strokeWidth = 3.dp
                    )
                }
            } else if (itemsToDisplay.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Default.Inventory else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = if (selectedTab == 0) "Belum ada tawaran aktif" else "Belum ada pertukaran berhasil",
                            color = Color.Gray,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsToDisplay.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { item ->
                                EnhancedHelpRequestCard(
                                    helpRequest = item,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun GradientStatCard(
    number: String,
    label: String,
    gradientColors: List<Color>,
    iconColor: Color
) {
    Surface(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    1.dp,
                    iconColor.copy(alpha = 0.2f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = number,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EnhancedHelpRequestCard(
    helpRequest: HelpRequestModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                AsyncImage(
                    model = helpRequest.imageUrl,
                    contentDescription = helpRequest.nameOfProduct,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Status Badge
                if (helpRequest.isCheckout) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = TealAccent
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Selesai",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = helpRequest.nameOfProduct,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = Color(0xFF1A1A1A)
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    profile: com.example.alp_visprog.models.ProfileModel,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Double, String?) -> Unit
) {
    var fullName by remember { mutableStateOf(profile.fullName ?: "") }
    var location by remember { mutableStateOf(profile.location ?: "") }
    var latitude by remember { mutableStateOf(profile.latitude ?: 0.0) }
    var longitude by remember { mutableStateOf(profile.longitude ?: 0.0) }
    var bio by remember { mutableStateOf(profile.bio ?: "") }
    var showLocationPicker by remember { mutableStateOf(false) }

    if (showLocationPicker) {
        LocationPickerView(
            onLocationSelected = { name, lat, lon ->
                location = name
                latitude = lat
                longitude = lon
                showLocationPicker = false
            },
            onClose = { showLocationPicker = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                onClick = { },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clickable(
                    onClick = { },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit Profil",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Profile Photo Section
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(110.dp)
                    ) {
                        if (!profile.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = profile.photoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, OrangeGradientStart.copy(alpha = 0.3f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F5F5))
                                    .border(3.dp, OrangeGradientStart.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Default Profile",
                                    modifier = Modifier.size(55.dp),
                                    tint = Color.Gray
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(36.dp),
                            shape = CircleShape,
                            color = OrangeGradientStart,
                            shadowElevation = 4.dp,
                            onClick = { }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ketuk untuk ganti foto",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Form Fields
                    Text(
                        text = "Nama Lengkap",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = OrangeGradientStart,
                            focusedContainerColor = OrangeGradientStart.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Lokasi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLocationPicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = if (location.isNotEmpty()) OrangeGradientStart else Color(0xFFE0E0E0),
                            disabledPlaceholderColor = Color.Gray,
                            disabledContainerColor = if (location.isNotEmpty()) OrangeGradientStart.copy(alpha = 0.05f) else Color(0xFFFAFAFA),
                            disabledLeadingIconColor = if (location.isNotEmpty()) OrangeGradientStart else Color.Gray
                        ),
                        placeholder = { Text("Klik untuk memilih lokasi", color = Color.Gray) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, null)
                        },
                        trailingIcon = {
                            if (location.isNotEmpty()) {
                                Icon(Icons.Default.CheckCircle, null, tint = TealAccent)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Bio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text(
                                text = "Ceritakan tentang diri Anda...",
                                color = Color.Gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = OrangeGradientStart,
                            focusedContainerColor = OrangeGradientStart.copy(alpha = 0.05f),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        ),
                        maxLines = 6
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            ),
                            border = BorderStroke(1.5.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("Batal", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }

                        Button(
                            onClick = {
                                onSave(fullName, location, latitude, longitude, bio.ifBlank { null })
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                OrangeGradientStart,
                                                OrangeGradientEnd
                                            )
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Simpan",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsModal(
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight()
                .clickable(
                    onClick = { },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pengaturan",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Keluar",
                        fontSize = 17.sp,
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    border = BorderStroke(1.5.dp, Color(0xFFE0E0E0))
                ) {
                    Text("Batal", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}