package com.example.alp_visprog.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alp_visprog.models.LocationIQResponse
import com.example.alp_visprog.services.LocationIQAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerView(
    onLocationSelected: (String, Double, Double) -> Unit, // Returns: Name, Lat, Lon
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<LocationIQResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // --- API Logic (Built-in for simplicity) ---
    fun searchLocation() {
        if (query.isBlank()) return

        isLoading = true
        errorMessage = null

        // 1. Setup Retrofit (Ideally put this in AppContainer, but this works for now)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://us1.locationiq.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LocationIQAPIService::class.java)

        // 2. YOUR API KEY (Get free from locationiq.com)
        val apiKey = "pk.3668d9ed557a06d27fd1f7646e666bf8"

        // 3. Call API
        service.searchLocation(apiKey, query).enqueue(object : Callback<List<LocationIQResponse>> {
            override fun onResponse(call: Call<List<LocationIQResponse>>, response: Response<List<LocationIQResponse>>) {
                isLoading = false
                if (response.isSuccessful) {
                    results = response.body() ?: emptyList()
                    if (results.isEmpty()) {
                        errorMessage = "Tidak ditemukan lokasi dengan nama tersebut."
                    }
                } else {
                    errorMessage = "Gagal memuat: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<List<LocationIQResponse>>, t: Throwable) {
                isLoading = false
                errorMessage = "Error koneksi: ${t.localizedMessage}"
            }
        })
    }

    // --- UI Layout ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cari Lokasi", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Light Gray bg
                .padding(16.dp)
        ) {
            // 1. Search Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Ketik nama kota atau jalan...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Button(
                    onClick = { searchLocation() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)) // Orange
                ) {
                    Text("Cari")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Content (Loading / Error / List)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF7043))
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage!!, color = Color.Red)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results) { item ->
                        LocationResultItem(item) {
                            // User clicked this item -> Return data & Close
                            onLocationSelected(
                                item.displayName,
                                item.lat.toDouble(),
                                item.lon.toDouble()
                            )
                            Toast.makeText(context, "Lokasi dipilih!", Toast.LENGTH_SHORT).show()
                            onClose()
                        }
                    }
                }
            }
        }
    }
}

// Reusable Card for the list items
@Composable
fun LocationResultItem(item: LocationIQResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF26A69A) // Teal
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.displayName,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}