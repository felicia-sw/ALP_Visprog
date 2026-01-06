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
import androidx.compose.ui.layout.ContentScale
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
    var selectedFilter by remember { mutableStateOf("Semua") }
    var showFilters by remember { mutableStateOf(true) } // State for filter visibility

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Supergraphic Background for entire home
        AsyncImage(
            model = R.drawable.pattern_tukerin,
            contentDescription = "Supergraphic Pattern",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
            contentScale = ContentScale.Crop
        )

        // Main content on top of supergraphic
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP BAR DESIGN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BrandOrange)
            ) {
                // Content on top
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
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
                            .height(52.dp), // Increased from 48.dp to prevent text cutoff
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BrandOrange
                        ),
                        singleLine = true
                    )

                    // Location and Filter Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location
                        Surface(
                            modifier = Modifier.weight(1f),
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

                        // Filter Toggle Button
                        Surface(
                            modifier = Modifier.size(40.dp),
                            color = if (showFilters) Color.White else Color.White.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { showFilters = !showFilters }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = if (showFilters) "Hide Filters" else "Show Filters",
                                    tint = if (showFilters) BrandOrange else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // FILTER BUTTONS - EVENLY SPACED (conditionally shown)
            if (showFilters) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = selectedFilter == "Semua",
                        onClick = {
                            selectedFilter = "Semua"
                            onFilterClick(null, null)
                        },
                        label = { Text("Semua") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandOrange,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF666666)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedFilter == "Semua",
                            borderColor = if (selectedFilter == "Semua") BrandOrange else Color(0xFFCCCCCC),
                            selectedBorderColor = BrandOrange,
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    )

                    FilterChip(
                        selected = selectedFilter == "BARANG",
                        onClick = {
                            selectedFilter = "BARANG"
                            onFilterClick("1", null) // categoryId 1 = Barang
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
                            onFilterClick("2", null) // categoryId 2 = Jasa
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