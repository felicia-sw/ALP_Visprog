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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.alp_visprog.uiStates.HomeUIState
import com.example.alp_visprog.viewModel.HomeViewModel
import com.example.alp_visprog.ui.theme.BrandOrange
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.alp_visprog.R
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

    LaunchedEffect(Unit) {
        viewModel.loadHelpRequests()
    }

    val state by viewModel.homeUIState.collectAsState()

    HomeContent(
        state = state,
        userLocation = viewModel.userLocation,
        onRefresh = { viewModel.loadHelpRequests() },
        onFilterClick = { type, status -> viewModel.filterHelpRequests(type, status) },
        onSearch = { query -> viewModel.searchHelpRequests(query) },
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
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    var showFilters by remember { mutableStateOf(true) }
    var isCompactView by remember { mutableStateOf(false) }

    // Debounce search to avoid excessive API calls
    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            onRefresh()
        } else {
            kotlinx.coroutines.delay(500)
            onSearch(searchQuery)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = R.drawable.pattern_tukerin,
            contentDescription = "Supergraphic Pattern",
            modifier = Modifier.fillMaxSize().alpha(0.3f),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // IMPROVED: Enhanced header with better contrast
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(BrandOrange)
                    .shadow(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari Barang atau Jasa...", fontSize = 14.sp, color = Color.Gray) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(20.dp),
                                tint = BrandOrange
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BrandOrange.copy(alpha = 0.5f)
                        ),
                        singleLine = true
                    )

                    // Location and View Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Location
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                                    color = Color(0xFF333333),
                                    maxLines = 1
                                )
                            }
                        }

                        // View Toggle
                        Surface(
                            modifier = Modifier.size(44.dp),
                            color = if (isCompactView) BrandOrange else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            onClick = { isCompactView = !isCompactView },
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isCompactView) Icons.Default.ViewAgenda else Icons.Default.ViewStream,
                                    contentDescription = "Toggle View",
                                    tint = if (isCompactView) Color.White else BrandOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Filter Toggle
                        Surface(
                            modifier = Modifier.size(44.dp),
                            color = if (showFilters) BrandOrange else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            onClick = { showFilters = !showFilters },
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Toggle Filters",
                                    tint = if (showFilters) Color.White else BrandOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // IMPROVED: Better filter chips with clearer labels
            if (showFilters) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Semua Filter
                        FilterChip(
                            selected = selectedFilter == "Semua",
                            onClick = {
                                selectedFilter = "Semua"
                                searchQuery = ""
                                onFilterClick(null, null)
                            },
                            label = {
                                Text(
                                    "Semua",
                                    fontWeight = if (selectedFilter == "Semua") FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (selectedFilter == "Semua") {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF5F5F5),
                                labelColor = Color(0xFF666666)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == "Semua",
                                borderColor = if (selectedFilter == "Semua") BrandOrange else Color.Transparent,
                                selectedBorderColor = BrandOrange,
                                borderWidth = 2.dp
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Barang Filter
                        FilterChip(
                            selected = selectedFilter == "BARANG",
                            onClick = {
                                selectedFilter = "BARANG"
                                searchQuery = ""
                                onFilterClick("BARANG", null)
                            },
                            label = {
                                Text(
                                    "Barang",
                                    fontWeight = if (selectedFilter == "BARANG") FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (selectedFilter == "BARANG") {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF5F5F5),
                                labelColor = Color(0xFF666666)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == "BARANG",
                                borderColor = if (selectedFilter == "BARANG") BrandOrange else Color.Transparent,
                                selectedBorderColor = BrandOrange,
                                borderWidth = 2.dp
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Jasa Filter
                        FilterChip(
                            selected = selectedFilter == "JASA",
                            onClick = {
                                selectedFilter = "JASA"
                                searchQuery = ""
                                onFilterClick("JASA", null)
                            },
                            label = {
                                Text(
                                    "Jasa",
                                    fontWeight = if (selectedFilter == "JASA") FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = if (selectedFilter == "JASA") {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFFF5F5F5),
                                labelColor = Color(0xFF666666)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == "JASA",
                                borderColor = if (selectedFilter == "JASA") BrandOrange else Color.Transparent,
                                selectedBorderColor = BrandOrange,
                                borderWidth = 2.dp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Content
            when (state) {
                is HomeUIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = BrandOrange)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Memuat...", color = Color.Gray)
                        }
                    }
                }

                is HomeUIState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.errorMessage,
                                color = Color.Red,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange)
                            ) {
                                Text("Coba Lagi")
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
                                    text = if (searchQuery.isNotEmpty())
                                        "Tidak ada hasil untuk \"$searchQuery\""
                                    else
                                        "Belum ada tawaran tersedia",
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(if (isCompactView) 8.dp else 12.dp)
                        ) {
                            items(state.data) { helpRequest ->
                                if (isCompactView) {
                                    HelpRequestCompactCard(
                                        request = helpRequest,
                                        onAddToCart = {},
                                        onContactSeller = {
                                            navController.navigate("create_exchange/${helpRequest.id}")
                                        },
                                        onProfileClick = { userId ->
                                            navController.navigate("Profile")
                                        }
                                    )
                                } else {
                                    HelpRequestCard(
                                        request = helpRequest,
                                        onAddToCart = {},
                                        onContactSeller = {
                                            navController.navigate("create_exchange/${helpRequest.id}")
                                        },
                                        onProfileClick = { userId ->
                                            navController.navigate("Profile")
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
}