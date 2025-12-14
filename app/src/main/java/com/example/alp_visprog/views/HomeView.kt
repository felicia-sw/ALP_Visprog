package com.example.alp_visprog.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.alp_visprog.models.HelpRequest
import com.example.alp_visprog.models.HelpRequestUser
import com.example.alp_visprog.uiStates.HomeUIState
import com.example.alp_visprog.viewModel.HomeViewModel
import com.example.alp_visprog.ui.theme.BrandOrange
import com.example.alp_visprog.ui.theme.BrandTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController()
) {
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
                                HelpRequestCard(helpRequest = helpRequest)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HelpRequestCard(helpRequest: HelpRequest) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color(0xFFE0E0E0)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User avatar",
                            modifier = Modifier.padding(10.dp),
                            tint = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = helpRequest.user?.name ?: "Anonymous",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(10.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = " 0.5 km",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Time",
                                modifier = Modifier.size(10.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = " 2 jam lalu",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                Surface(
                    color = if (helpRequest.status == "MENAWARKAN") BrandOrange else BrandTeal,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (helpRequest.status == "MENAWARKAN") "MENAWARKAN" else "MENCARI",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            if (helpRequest.imageUrl != null) {
                AsyncImage(
                    model = helpRequest.imageUrl,
                    contentDescription = helpRequest.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (helpRequest.type == "BARANG") Icons.Default.ShoppingBag else Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                }
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = helpRequest.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = if (helpRequest.status == "MENAWARKAN") BrandOrange else BrandTeal,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Stok: ${helpRequest.stock}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (helpRequest.status == "MENCARI" && helpRequest.seekingItem != null) {
                    Surface(
                        color = Color(0xFFE0F2F1),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = BrandTeal
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "Mencari: ${helpRequest.seekingItem}",
                                fontSize = 10.sp,
                                color = Color(0xFF00695C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                helpRequest.description?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text(
                            text = if (helpRequest.status == "MENAWARKAN") "Tawarkan Tukar" else "Add to cart",
                            fontSize = 13.sp
                        )
                    }
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrandOrange
                        ),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Text("Hubungi Penjual", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeViewPreview() {
    val dummyData = listOf(
        HelpRequest(
            id = 1,
            title = "Need Rice",
            description = "Looking for 5kg of rice",
            type = "BARANG",
            status = "MENCARI",
            stock = 5,
            imageUrl = null,
            seekingItem = "Rice",
            userId = 1,
            user = HelpRequestUser(1, "eileen", "Sby"),
            createdAt = "2023-12-01",
            updatedAt = "2023-12-01"
        )
    )

    HomeContent(
        state = HomeUIState.Success(dummyData),
        onRefresh = {},
        onFilterClick = { _, _ -> }
    )
}