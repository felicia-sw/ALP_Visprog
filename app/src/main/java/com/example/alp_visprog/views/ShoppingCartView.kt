package com.example.alp_visprog.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_visprog.models.CartItemResponse
import com.example.alp_visprog.uiStates.ShoppingCartUIState
import com.example.alp_visprog.viewModel.ShoppingCartViewModel

// --- Main Entry Point (Logic) ---
@Composable
fun ShoppingCartView(
    viewModel: ShoppingCartViewModel = viewModel(factory = ShoppingCartViewModel.Factory),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ShoppingCartContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onDeleteClick = { helpRequestId -> viewModel.removeFromCart(helpRequestId) }
    )
}

// --- UI Content (Stateless & Previewable) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartContent(
    uiState: ShoppingCartUIState,
    onBackClick: () -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang Saya", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (uiState) {
                is ShoppingCartUIState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFFE97856))
                }
                is ShoppingCartUIState.Error -> {
                    Text(
                        text = uiState.errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ShoppingCartUIState.Success -> {
                    if (uiState.items.isEmpty()) {
                        Text("Keranjang kosong", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.items) { item ->
                                CartItemCard(
                                    item = item,
                                    onDelete = { onDeleteClick(item.helpRequestId) }
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
fun CartItemCard(item: CartItemResponse, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCF8F3)),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Tukar dgn: ${item.price}", fontSize = 14.sp, color = Color(0xFFE97856))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.description, fontSize = 12.sp, color = Color.Gray, maxLines = 2)
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

// --- PREVIEW FUNCTION ---
@Preview(showBackground = true)
@Composable
fun ShoppingCartPreview() {
    // Fake Data for Preview
    val fakeItems = listOf(
        CartItemResponse(
            helpRequestId = 1,
            addedAt = "2023-01-01",
            productName = "Sepatu Nike Bekas",
            description = "Masih bagus, ukuran 42, warna hitam putih.",
            price = "Beras 5kg",
            imageUrl = ""
        ),
        CartItemResponse(
            helpRequestId = 2,
            addedAt = "2023-01-02",
            productName = "Jasa Service Laptop",
            description = "Bisa install ulang windows dan ganti thermal paste.",
            price = "Uang 50rb",
            imageUrl = ""
        )
    )

    ShoppingCartContent(
        uiState = ShoppingCartUIState.Success(fakeItems),
        onBackClick = {},
        onDeleteClick = {}
    )
}