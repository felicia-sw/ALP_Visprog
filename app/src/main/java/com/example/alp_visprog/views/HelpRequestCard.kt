package com.example.alp_visprog.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alp_visprog.models.HelpRequestModel

// --- Custom Colors based on your spec ---
val CardBeige = Color(0xFFFCF8F3) // Light cream background
val BrandOrange = Color(0xFFFF6B4A) // Orange action color
val BrandTeal = Color(0xFF4ECDC4)   // Turquoise/Teal
val TextDarkGray = Color(0xFF333333)

@Composable
fun HelpRequestCard(
    request: HelpRequestModel,
    onAddToCart: () -> Unit,
    onContactSeller: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // --- HEADER SECTION ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Seller Profile Image (Placeholder)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Seller",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 2. Seller Name & Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "User #${request.userId}", // Placeholder name
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDarkGray
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(
                            text = " ${request.location}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Outlined.AccessTime, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                        Text(
                            text = " Baru saja",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                // 3. Stock Badge
                Surface(
                    color = BrandTeal,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Text(
                        text = "Stok: 1", // Placeholder
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- "MENAWARKAN" BADGE ---
            // Simulating the "Orange Action Button" feel from the prompt
            Surface(
                color = BrandOrange,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.5f) // 50% width
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                    Text("MENAWARKAN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- PRODUCT IMAGE ---
            AsyncImage(
                model = request.imageUrl,
                contentDescription = request.nameOfProduct,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray) // Placeholder color while loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- PRODUCT TITLE ---
            Text(
                text = request.nameOfProduct,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- DIVIDER WITH ICON ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BrandTeal, thickness = 1.dp)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(24.dp)
                        .border(1.dp, BrandTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapVert, // Up-down arrows
                        contentDescription = "Exchange",
                        tint = BrandTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.weight(1f), color = BrandTeal, thickness = 1.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- "MENCARI" SECTION ---
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    color = BrandTeal,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "MENCARI",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = request.exchangeProductName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkGray
                    )
                    Text(
                        text = request.description,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BOTTOM ACTION BUTTONS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Add to Cart Button
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Keranjang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // 2. Contact Seller Button
                OutlinedButton(
                    onClick = onContactSeller,
                    border = BorderStroke(2.dp, BrandOrange),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandOrange),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Hubungi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ... (Your HelpRequestCard code above)

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun HelpRequestCardPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        // 1. Standard Case
        HelpRequestCard(
            request = HelpRequestModel(
                id = 1,
                nameOfProduct = "Sayuran Organik Segar",
                description = "Mencari telur ayam kampung atau buah-buahan lokal.",
                exchangeProductName = "Telur Ayam Kampung",
                location = "Jakarta Selatan",
                imageUrl = "",
                isCheckout = false,
                userId = 1,
                categoryId = 1,
                contactPhone = "0812345678",
                contactEmail = "siti@example.com"
            ),
            onAddToCart = {},
            onContactSeller = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Long Text Case (To test alignment)
        HelpRequestCard(
            request = HelpRequestModel(
                id = 2,
                nameOfProduct = "Jasa Service Laptop & Install Ulang Windows",
                description = "Bisa benerin laptop mati total, ganti LCD, atau install software. Saya mencari barter dengan beras 5kg atau minyak goreng 2 liter.",
                exchangeProductName = "Sembako (Beras/Minyak)",
                location = "Tangerang Kota",
                imageUrl = "",
                isCheckout = false,
                userId = 2,
                categoryId = 2,
                contactPhone = "0812345678",
                contactEmail = null
            ),
            onAddToCart = {},
            onContactSeller = {}
        )
    }
}