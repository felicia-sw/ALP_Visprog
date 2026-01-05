package com.example.alp_visprog.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alp_visprog.uiStates.HomeUIState
import com.example.alp_visprog.viewModel.HomeViewModel
import com.example.alp_visprog.ui.theme.BrandOrange
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.draw.alpha
import coil.compose.AsyncImage
import com.example.alp_visprog.R

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    // Accept an optional HomeViewModel for testing; when null we'll obtain it at runtime.
    homeViewModel: HomeViewModel? = null,
    navController: NavController = rememberNavController()
) {
    // If we're in the Preview (inspection) mode, avoid instantiating the real ViewModel
    val isPreview = LocalInspectionMode.current

    if (isPreview) {
        // Show a simple preview state without constructing model classes
        HomeContent(
            state = HomeUIState.Loading,
            userLocation = "Preview Location",
            onRefresh = {},
            onFilterClick = { _, _ -> },
            navController = navController,
            modifier = modifier
        )
        return
    }

    // Normal runtime path: obtain the real ViewModel if not supplied
    val viewModel = homeViewModel ?: viewModel(factory = HomeViewModel.Factory)

    LaunchedEffect(Unit) {
        viewModel.loadHelpRequests()
    }

    val state by viewModel.homeUIState.collectAsState()

    HomeContent(
        state = state,
        userLocation = viewModel.userLocation,
        onRefresh = { viewModel.loadHelpRequests() },
        onFilterClick = { type, status -> viewModel.filterHelpRequests(type, status) },
        navController = navController,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUIState,
    userLocation: String,
    onRefresh: () -> Unit,
    onFilterClick: (String?, String?) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // NEW TOP BAR DESIGN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Translucent Supergraphic Background
            // TODO: Replace with your actual supergraphic image
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandOrange.copy(alpha = 0.9f))
                    .alpha(0.3f)
            ) {
                // Placeholder for supergraphic - add your image here
                // AsyncImage(model = R.drawable.supergraphic, ...)
            }

            // Content on top of supergraphic
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // LEFT: Logo (1:1 ratio, small)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = R.drawable.app_icon_4,
                        contentDescription = "Logo",
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // RIGHT: Search and Location
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Cari Barang atau Jasa...",
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BrandOrange
                        ),
                        singleLine = true
                    )

                    // Location
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = BrandOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = userLocation,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }
        }

        // FILTER BUTTONS - EVENLY SPACED
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = {
                    selectedFilter = "ALL"
                    onFilterClick(null, null)
                },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandOrange,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color(0xFF666666)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == "ALL",
                    borderColor = if (selectedFilter == "ALL") BrandOrange else Color(0xFFCCCCCC),
                    selectedBorderColor = BrandOrange,
                    borderWidth = 1.dp
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )

            FilterChip(
                selected = selectedFilter == "BARANG",
                onClick = {
                    selectedFilter = "BARANG"
                    onFilterClick("BARANG", null)
                },
                label = { Text("Barang") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandOrange,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color(0xFF666666)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == "BARANG",
                    borderColor = if (selectedFilter == "BARANG") BrandOrange else Color(0xFFCCCCCC),
                    selectedBorderColor = BrandOrange,
                    borderWidth = 1.dp
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )

            FilterChip(
                selected = selectedFilter == "JASA",
                onClick = {
                    selectedFilter = "JASA"
                    onFilterClick("JASA", null)
                },
                label = { Text("Jasa") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandOrange,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = Color(0xFF666666)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedFilter == "JASA",
                    borderColor = if (selectedFilter == "JASA") BrandOrange else Color(0xFFCCCCCC),
                    selectedBorderColor = BrandOrange,
                    borderWidth = 1.dp
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
            )
        }

        // CONTENT BASED ON STATE
        when (state) {
            is HomeUIState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            }

            is HomeUIState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error: ${state.errorMessage}",
                            color = Color.Red,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            is HomeUIState.Success -> {
                if (state.data.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "No data",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No help requests available",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data) { helpRequest ->
                            HelpRequestCard(
                                request = helpRequest,
                                onAddToCart = {
                                    // TODO: Add to cart functionality
                                },
                                onContactSeller = {
                                    navController?.navigate("create_exchange/${helpRequest.id}")
                                },
                                onProfileClick = { userId ->
                                    navController?.navigate("Profile")
                                }
                            )
                        }
                    }
                }
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeViewPreview() {
    HomeContent(
        state = HomeUIState.Loading,
        userLocation = "Preview Location",
        onRefresh = {},
        onFilterClick = { _, _ -> }
    )
}