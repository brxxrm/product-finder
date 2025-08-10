package com.example.productfinder.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import com.example.productfinder.components.CameraPreview

@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
    onBack: () -> Unit
) {
    CameraPreview(
        onImageCaptured = onImageCaptured,
        onBack = onBack
    )
}