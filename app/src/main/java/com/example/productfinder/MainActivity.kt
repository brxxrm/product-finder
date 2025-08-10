package com.example.productfinder

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.productfinder.screens.HomeScreen
import com.example.productfinder.screens.ResultsScreen
import com.example.productfinder.ui.theme.ProductFinderTheme
import com.example.productfinder.viewmodel.MainViewModel
import java.io.File

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProductFinderApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun ProductFinderApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var hasPermission by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES] == true ||
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true ||
                permissions[Manifest.permission.CAMERA] == true
    }

    // Launcher para tomar foto con cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && capturedImageUri != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    capturedImageUri
                )
                viewModel.analyzeImage(bitmap)
                viewModel.setCurrentImageUri(capturedImageUri.toString())
                navController.navigate("results")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Launcher para seleccionar de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri ->
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    imageUri
                )
                viewModel.analyzeImage(bitmap)
                viewModel.setCurrentImageUri(imageUri.toString())
                navController.navigate("results")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onTakePhoto = {
                    if (hasPermission) {
                        // Crear archivo temporal para la foto
                        val photoFile = File.createTempFile(
                            "photo_${System.currentTimeMillis()}",
                            ".jpg",
                            context.cacheDir
                        )
                        capturedImageUri = androidx.core.content.FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        cameraLauncher.launch(capturedImageUri!!)
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    }
                },
                onUploadPhoto = {
                    if (hasPermission) {
                        galleryLauncher.launch("image/*")
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    }
                },
                onSearchProduct = { searchTerm ->
                    viewModel.addRecentSearch(searchTerm)
                },
                recentSearches = uiState.recentSearches, // Usar búsquedas del estado
                onClearHistory = { viewModel.clearRecentSearches() }
            )
        }

        composable("results") {
            ResultsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
