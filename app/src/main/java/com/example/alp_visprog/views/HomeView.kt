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
import com.example.alp_visprog.models.HelpRequestModel
import com.example.alp_visprog.uiStates.HomeUIState
import com.example.alp_visprog.viewModel.HomeViewModel
import com.example.alp_visprog.ui.theme.BrandOrange
import com.example.alp_visprog.ui.theme.BrandTeal
import androidx.compose.ui.platform.LocalInspectionMode

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
            onRefresh = {},
            onFilterClick = { _, _ -> },
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
        onRefresh = { viewModel.loadHelpRequests() },
        onFilterClick = { type, status -> viewModel.filterHelpRequests(type, status) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUIState,
    onRefresh: () -> Unit,
    onFilterClick: (String?, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = BrandOrange,
            shadowElevation = 5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Lokasi: Banjarbaru",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White
                    )
                }
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
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Error: ${state.errorMessage}",
                            color = Color.Red,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(15.dp))
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari tetangga atau barang...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 15.dp)
                        ) {
                            Text("Filter")
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F7FA)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = BrandTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Sistem Barter",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Tukar barang dengan barang, jasa dengan jasa. Tanpa uang, hanya pertukaran!",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == "BARANG",
                            onClick = {
                                selectedFilter = "BARANG"
                                onFilterClick("BARANG", null)
                            },
                            label = { Text("Barang") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedFilter == "JASA",
                            onClick = {
                                selectedFilter = "JASA"
                                onFilterClick("JASA", null)
                            },
                            label = { Text("Jasa") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandOrange,
                                selectedLabelColor = Color.White
                            ))
                    }

                    Spacer(modifier = Modifier.height(15.dp))

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
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "No help requests available",
                                    color = Color.Gray,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(state.data) { helpRequest ->
                                HelpRequestCard(helpRequest)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpRequestCard(helpRequest: HelpRequestModel) {
    // Render using the actual fields from backend: nameOfProduct, exchangeProductName, description, location, user
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = helpRequest.nameOfProduct,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = helpRequest.user?.username ?: "Unknown User",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = helpRequest.location,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = helpRequest.description,
                fontSize = 13.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tukar dengan: ${helpRequest.exchangeProductName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = BrandOrange,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Button(
                onClick = {
                    // Navigate to "Reply to Post #1"
                    navControlIer.navigate("create_exchange/1")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C35))
            ) {
                Text("Test: Offer Help for Post #1")
            }
            // --- TEMP BUTTON TO TEST FEATURE ---
            Button(
                onClick = {
                    // Navigate to the route we defined in AppRouting
                    // We hardcode "1" for testing (representing Post ID #1)
                    navControlIer.navigate("exchange_list/1")
                }
            ) {
                Text(text = "See Offers for Post #1")
            }


        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeViewPreview() {
    HomeContent(
        state = HomeUIState.Loading,
        onRefresh = {},
        onFilterClick = { _, _ -> }
    )
}