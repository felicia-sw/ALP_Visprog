package com.example.alp_visprog.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.alp_visprog.R
import com.example.alp_visprog.models.ExchangeModel
import com.example.alp_visprog.ui.theme.ALP_VisprogTheme
import com.example.alp_visprog.uiStates.ExchangeUIState
import com.example.alp_visprog.viewModel.ExchangeViewModel
import androidx.compose.runtime.collectAsState // <--- Make sure this is imported
import androidx.compose.runtime.getValue      // <--- And this

@Composable
fun ExchangeListView(
    helpRequestId: Int,
    viewModel: ExchangeViewModel = viewModel(factory = ExchangeViewModel.Factory),
    onBackClick: () -> Unit = {}
) {
    // 1. Fetch data on launch
    LaunchedEffect(helpRequestId) {
        viewModel.getExchangeOffers(helpRequestId)
    }

    // 2. COLLECT the StateFlow (This is the new "Best Practice" part)
    // Instead of "viewModel.exchangeUIState", we collect it as a state
    val state by viewModel.exchangeUIState.collectAsState()

    // Pass the collected 'state' down to your content
    ExchangeListContent(
        state = state, // <--- Pass the value we just collected
        onDeleteClick = { exchangeId ->
            viewModel.deleteExchange(exchangeId, helpRequestId)
        },
        onBackClick = onBackClick
    )
}

// 2. STATELESS COMPOSABLE (Pure UI)
// Use this one for Previews!
@Composable
fun ExchangeListContent(
    state: ExchangeUIState,
    onDeleteClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Background pattern
            AsyncImage(
                model = R.drawable.pattern_tukerin,
                contentDescription = "Background Pattern",
                modifier = Modifier.fillMaxSize().alpha(0.3f),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Exchange Offers",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (state) {
                    is ExchangeUIState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is ExchangeUIState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Error: ${state.errorMessage}", color = Color.Red)
                        }
                    }

                    is ExchangeUIState.Success -> {
                        if (state.data.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "No offers yet.")
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.data) { exchange ->
                                    ExchangeCard(
                                        exchange = exchange,
                                        onDeleteClick = { onDeleteClick(exchange.id) }
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

// --- PREVIEWS ---

@Preview(showBackground = true)
@Composable
fun ExchangeListSuccessPreview() {
    // Fake Data
    val dummyData = listOf(
        ExchangeModel(1, "Alice", "08111", "alice@test.com", "I have it!", 1),
        ExchangeModel(2, "Bob", "08222", "bob@test.com", "Can we trade?", 1)
    )

    ALP_VisprogTheme {
        ExchangeListContent(
            state = ExchangeUIState.Success(dummyData),
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeListLoadingPreview() {
    ALP_VisprogTheme {
        ExchangeListContent(
            state = ExchangeUIState.Loading,
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeListErrorPreview() {
    ALP_VisprogTheme {
        ExchangeListContent(
            state = ExchangeUIState.Error("Failed to connect to server"),
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}
