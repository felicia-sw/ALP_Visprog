package com.example.alp_visprog.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alp_visprog.models.ExchangeModel
import com.example.alp_visprog.ui.theme.ALP_VisprogTheme

@Composable
fun ExchangeCard(
    exchange: ExchangeModel,
    onDeleteClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exchange.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Phone: ${exchange.phone}",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
                if (!exchange.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "\"${exchange.description}\"",
                        fontSize = 15.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // DELETE BUTTON
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Offer",
                    tint = Color.Red
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeCardPreview() {
    ALP_VisprogTheme {
        ExchangeCard(
            exchange = ExchangeModel(
                id = 1,
                name = "Eileen ",
                phone = "08123456789",
                email = "eileen@gmail.com",
                description = "I have the ladder you need, chat me to discuss where we can meet up.",
                helpRequestId = 101
            ),
            onDeleteClick = {}
        )
    }
}