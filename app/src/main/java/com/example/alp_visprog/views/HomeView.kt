package com.example.alp_visprog.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.alp_visprog.viewModel.HomeViewModel

@Composable
fun HomeView(
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier,
    navControlIer: NavController = rememberNavController()
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Welcome to the home screen",
                style = MaterialTheme.typography.bodyLarge
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
    HomeView()
}