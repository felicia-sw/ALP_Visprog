package com.example.alp_visprog.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alp_visprog.models.ExchangeModel
import com.example.alp_visprog.ui.theme.ALP_VisprogTheme

@Composable
fun ExchangeCard(
    exchange: ExchangeModel,
    onDeleteClick: () -> Unit
) {
    // Warna untuk aksesibilitas (usia 35-55)
    val darkText = Color(0xFF1C1B1F)
    val grayText = Color(0xFF49454F)
    val redWarning = Color(0xFFB00020)

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // KOLOM INFO
            Column(modifier = Modifier.weight(1f)) {
                // 1. Nama
                Text(
                    text = exchange.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkText
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Telepon (Selalu ditampilkan)
                Text(
                    text = "Telepon: ${exchange.phone}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = grayText,
                        fontSize = 15.sp
                    )
                )

                // 3. Email (OPSIONAL - hanya tampil jika ada)
                if (!exchange.email.isNullOrEmpty()) {
                    Text(
                        text = "Email: ${exchange.email}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = grayText,
                            fontSize = 15.sp
                        )
                    )
                }

                // 4. Deskripsi (OPSIONAL)
                if (!exchange.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${exchange.description}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            color = darkText,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            // TOMBOL HAPUS
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Penawaran",
                    tint = redWarning
                )
            }
        }
    }
}

// --- PRATINJAU ---
@Preview(showBackground = true, backgroundColor = 0xFFFEFDF5)
@Composable
fun ExchangeCardPreview() {
    ALP_VisprogTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Kasus 1: Semua Data Ada
            ExchangeCard(
                exchange = ExchangeModel(
                    id = 1,
                    name = "Tetangga dengan Informasi Lengkap",
                    phone = "08123456789",
                    email = "neighbor@example.com",
                    description = "Saya punya tangga yang Anda butuhkan! Mari bertemu.",
                    helpRequestId = 101
                ),
                onDeleteClick = {}
            )

            // Kasus 2: Tidak ada Email, Tidak ada Deskripsi (Minimal)
            ExchangeCard(
                exchange = ExchangeModel(
                    id = 2,
                    name = "Tetangga Informasi Minimal",
                    phone = "08111222333",
                    email = null, // Tidak akan menampilkan baris "Email:"
                    description = null,
                    helpRequestId = 101
                ),
                onDeleteClick = {}
            )
        }
    }
}
