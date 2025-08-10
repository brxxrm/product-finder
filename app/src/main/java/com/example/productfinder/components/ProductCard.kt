@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.productfinder.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProductCard(
    productName: String,
    confidence: Float? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = productName,
                style = MaterialTheme.typography.titleMedium
            )

            confidence?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Confidence: ${(it * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
fun ProductCardPreview() {
    ProductCard(
        productName = "Sample Product",
        confidence = 0.92f,
        onClick = {}
    )
}