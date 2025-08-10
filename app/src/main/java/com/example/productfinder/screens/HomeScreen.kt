@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.productfinder.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.productfinder.R

@Composable
fun HomeScreen(
    onTakePhoto: () -> Unit,
    onUploadPhoto: () -> Unit,
    onSearchProduct: (String) -> Unit = {},
    recentSearches: List<String> = emptyList(),
    onClearHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionToRequest by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (permissionToRequest == Manifest.permission.CAMERA) {
                onTakePhoto()
            } else {
                onUploadPhoto()
            }
        } else {
            showPermissionDialog = true
        }
    }

    val openSettingsIntent = remember {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    }

    if (showPermissionDialog) {
        ModernPermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onConfirm = {
                showPermissionDialog = false
                context.startActivity(openSettingsIntent)
            }
        )
    }

    // Función simplificada para buscar productos en Google
    fun searchProductInGoogle(searchTerm: String) {
        try {
            // Búsqueda simple solo con el nombre del producto
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.google.com/search?q=${Uri.encode(searchTerm)}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback con búsqueda web
            try {
                val fallbackIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra("query", searchTerm)
                }
                context.startActivity(fallbackIntent)
            } catch (e2: Exception) {
                // Último fallback - búsqueda simple
                val simpleIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.google.com/search?q=${Uri.encode(searchTerm)}")
                }
                context.startActivity(simpleIntent)
            }
        }
    }

    // Fondo blanco sólido con LazyColumn para scroll
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                ModernHeader { context.startActivity(openSettingsIntent) }
            }

            // Contenido principal
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Título principal
                    WelcomeSection()

                    Spacer(modifier = Modifier.height(40.dp))

                    // Botones de acción
                    ActionButtonsSection(
                        onTakePhoto = {
                            if (hasCameraPermission(context)) {
                                onTakePhoto()
                            } else {
                                permissionToRequest = Manifest.permission.CAMERA
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        onUploadPhoto = {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            if (hasStoragePermission(context)) {
                                onUploadPhoto()
                            } else {
                                permissionToRequest = permission
                                permissionLauncher.launch(permission)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Sección de búsquedas recientes (dinámicas)
            if (recentSearches.isEmpty()) {
                // Estado vacío
                item {
                    EmptyHistorySection()
                }
            } else {
                // Header con opción de limpiar
                item {
                    RecentSearchesHeader(
                        onClearHistory = onClearHistory
                    )
                }

                // Grid de búsquedas recientes
                items(recentSearches.chunked(2)) { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    ) {
                        rowItems.forEach { search ->
                            SearchCard(
                                searchText = search,
                                onClick = {
                                    onSearchProduct(search)
                                    searchProductInGoogle(search)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Rellenar espacio si solo hay un elemento en la fila
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernHeader(onSettingsClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSettingsClicked,
            modifier = Modifier
                .size(40.dp)
                .background(
                    Color(0xFFF5F5F5),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = Color(0xFF666666),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun WelcomeSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono principal minimalista
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    Color(0xFFF0F8FF),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = Color(0xFF007AFF),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Encuentra cualquier producto",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E),
            textAlign = TextAlign.Center,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Toma una foto o sube una imagen para descubrir productos similares al instante",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF8E8E93),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onTakePhoto: () -> Unit,
    onUploadPhoto: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón principal - Tomar foto
        Button(
            onClick = onTakePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007AFF),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Tomar Foto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Usa tu cámara",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón secundario - Subir foto
        OutlinedButton(
            onClick = onUploadPhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF1C1C1E)
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Subir desde galería",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptyHistorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono de estado vacío
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Color(0xFFF2F2F7),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin búsquedas recientes",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1C1C1E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Toma una foto de un producto para comenzar a buscar",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF8E8E93),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun RecentSearchesHeader(onClearHistory: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.History,
            contentDescription = null,
            tint = Color(0xFF8E8E93),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Búsquedas recientes",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1C1C1E)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón para limpiar historial
        TextButton(
            onClick = onClearHistory,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color(0xFF8E8E93)
            )
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Limpiar",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SearchCard(
    searchText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(64.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF2F2F7)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = searchText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1C1C1E),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ModernPermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.permission_required),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1C1E)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.camera_storage_permission_explanation),
                color = Color(0xFF8E8E93),
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF007AFF)
                )
            ) {
                Text(
                    text = stringResource(R.string.open_settings),
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF8E8E93)
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

private fun hasCameraPermission(context: Context) =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

private fun hasStoragePermission(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
