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
            // If search is cleared, reload all
            onRefresh()
        } else {
            // Wait 500ms before searching
            kotlinx.coroutines.delay(500)
            onSearch(searchQuery)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.pattern_tukerin,
            contentDescription = "Supergraphic Pattern",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(BrandOrange)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = BrandOrange
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                        Surface(
                            modifier = Modifier.size(40.dp),
                            color = if (isCompactView) Color.White else Color.White.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { isCompactView = !isCompactView }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCompactView) Icons.Default.ViewAgenda else Icons.Default.ViewStream,
                                    contentDescription = if (isCompactView) "Large View" else "Compact View",
                                    tint = if (isCompactView) BrandOrange else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

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
                            searchQuery = "" // Clear search when filtering
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
                            searchQuery = "" // Clear search when filtering
                            onFilterClick("1", null)
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
                            searchQuery = "" // Clear search when filtering
                            onFilterClick("2", null)
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
                                    text = if (searchQuery.isNotEmpty()) "Tidak ada hasil untuk \"$searchQuery\"" else "No help requests available",
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
