package com.example.productfinder.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.productfinder.viewmodel.MainViewModel
import com.example.productfinder.viewmodel.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Función simplificada para buscar productos
    fun searchProduct(searchTerm: String) {
        // Agregar al historial cuando se busque manualmente
        viewModel.addRecentSearch(searchTerm)

        try {
            // Búsqueda simple solo con el nombre del producto
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.google.com/search?q=${Uri.encode(searchTerm)}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
            try {
                val fallbackIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra("query", searchTerm)
                }
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                val simpleIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.google.com/search?q=${Uri.encode(searchTerm)}")
                }
                context.startActivity(simpleIntent)
            }
        }
    }

    // Fondo blanco sólido como en HomeScreen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header minimalista
        TopAppBar(
            title = {
                Text(
                    text = "Resultados",
                    color = Color(0xFF1C1C1E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF1C1C1E)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Vista previa de la imagen capturada/subida
            uiState.currentImageUri?.let { imageUri ->
                ImagePreviewSection(imageUri = imageUri)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Estado de carga/análisis
            if (uiState.isAnalyzing) {
                LoadingCard(text = "Analizando imagen...")
            }

            // Indicador de traducción
            AnimatedVisibility(
                visible = uiState.isTranslating,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                LoadingCard(
                    text = "Traduciendo productos...",
                    icon = Icons.Default.Translate
                )
            }

            // Contador de resultados
            if (uiState.products.isNotEmpty() && !uiState.isAnalyzing) {
                Text(
                    text = "${uiState.products.size} productos encontrados",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF8E8E93),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Lista de productos o estado vacío
            if (uiState.products.isEmpty() && !uiState.isAnalyzing) {
                EmptyStateSection(onRetry = onBackClick)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.products) { product ->
                        ProductCard(
                            product = product,
                            onSearchProduct = { productName ->
                                searchProduct(productName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImagePreviewSection(imageUri: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2F2F7)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Imagen capturada",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun LoadingCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.Search
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2F2F7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (icon == Icons.Default.Translate) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFF007AFF),
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = text,
                fontSize = 15.sp,
                color = Color(0xFF1C1C1E),
                fontWeight = FontWeight.Medium
            )
            if (icon == Icons.Default.Translate) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color(0xFF007AFF),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onSearchProduct: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Nombre original
            Text(
                text = product.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre traducido
            if (product.isTranslating) {
                ShimmerText()
            } else if (product.translatedName.isNotEmpty() && product.translatedName != product.name) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = Color(0xFF007AFF),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = product.translatedName,
                        fontSize = 15.sp,
                        color = Color(0xFF007AFF),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de confianza
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Confianza:",
                    fontSize = 13.sp,
                    color = Color(0xFF8E8E93)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF2F2F7))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(product.confidence)
                            .background(
                                Color(0xFF34C759),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }

                Text(
                    text = "${(product.confidence * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF34C759)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de búsqueda
            val searchTerm = product.translatedName.ifEmpty { product.name }

            Button(
                onClick = { onSearchProduct(searchTerm) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Buscar en Google",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun EmptyStateSection(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFF8E8E93),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No se encontraron productos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Intenta con otra imagen",
            fontSize = 14.sp,
            color = Color(0xFF8E8E93),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Intentar de nuevo",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ShimmerText() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF8E8E93).copy(alpha = alpha))
    )
}
