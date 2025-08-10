package com.example.productfinder.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Product(
    val name: String,
    val translatedName: String = "",
    val confidence: Float = 0.0f,
    val isTranslating: Boolean = false
)

data class UiState(
    val products: List<Product> = emptyList(),
    val isTranslating: Boolean = false,
    val isAnalyzing: Boolean = false,
    val currentImageUri: String? = null,
    val recentSearches: List<String> = emptyList()
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    private val translator = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
    )

    // Lista de búsquedas recientes (ahora dinámica)
    private val _recentSearches = mutableListOf<String>()

    init {
        // Descargar modelo de traducción
        translator.downloadModelIfNeeded()
    }

    fun setCurrentImageUri(uri: String) {
        _uiState.value = _uiState.value.copy(currentImageUri = uri)
    }

    fun getRecentSearches(): List<String> = _recentSearches.toList()

    fun addRecentSearch(search: String) {
        // Remover si ya existe para evitar duplicados
        _recentSearches.remove(search)
        // Agregar al inicio de la lista
        _recentSearches.add(0, search)
        // Mantener máximo 6 búsquedas
        if (_recentSearches.size > 6) {
            _recentSearches.removeAt(_recentSearches.size - 1)
        }
        // Actualizar el estado
        _uiState.value = _uiState.value.copy(recentSearches = _recentSearches.toList())
    }

    fun clearRecentSearches() {
        _recentSearches.clear()
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }

    fun analyzeImage(bitmap: Bitmap) {
        _uiState.value = _uiState.value.copy(
            isAnalyzing = true,
            products = emptyList()
        )

        val image = InputImage.fromBitmap(bitmap, 0)

        imageLabeler.process(image)
            .addOnSuccessListener { labels ->
                val products = labels.take(5).map { label ->
                    Product(
                        name = label.text,
                        confidence = label.confidence,
                        isTranslating = true
                    )
                }

                _uiState.value = _uiState.value.copy(
                    products = products,
                    isAnalyzing = false,
                    isTranslating = true
                )

                // Agregar productos detectados al historial de búsquedas
                products.forEach { product ->
                    addRecentSearch(product.name)
                }

                // Traducir productos
                translateProducts(products)
            }
            .addOnFailureListener {
                // Productos de ejemplo si falla la detección
                val sampleProducts = listOf(
                    Product("Phone", confidence = 0.8f, isTranslating = true),
                    Product("Book", confidence = 0.7f, isTranslating = true),
                    Product("Cup", confidence = 0.6f, isTranslating = true)
                )

                _uiState.value = _uiState.value.copy(
                    products = sampleProducts,
                    isAnalyzing = false,
                    isTranslating = true
                )

                // Agregar productos de ejemplo al historial
                sampleProducts.forEach { product ->
                    addRecentSearch(product.name)
                }

                translateProducts(sampleProducts)
            }
    }

    private fun translateProducts(products: List<Product>) {
        viewModelScope.launch {
            val updatedProducts = mutableListOf<Product>()
            var completedTranslations = 0

            products.forEach { product ->
                translator.translate(product.name)
                    .addOnSuccessListener { translatedText ->
                        val translatedProduct = product.copy(
                            translatedName = translatedText,
                            isTranslating = false
                        )
                        updatedProducts.add(translatedProduct)
                        completedTranslations++

                        // También agregar la traducción al historial si es diferente
                        if (translatedText != product.name && translatedText.isNotBlank()) {
                            addRecentSearch(translatedText)
                        }

                        if (completedTranslations == products.size) {
                            _uiState.value = _uiState.value.copy(
                                products = updatedProducts.sortedBy { products.indexOf(product) },
                                isTranslating = false
                            )
                        }
                    }
                    .addOnFailureListener {
                        val fallbackProduct = product.copy(
                            translatedName = product.name,
                            isTranslating = false
                        )
                        updatedProducts.add(fallbackProduct)
                        completedTranslations++

                        if (completedTranslations == products.size) {
                            _uiState.value = _uiState.value.copy(
                                products = updatedProducts.sortedBy { products.indexOf(product) },
                                isTranslating = false
                            )
                        }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageLabeler.close()
        translator.close()
    }
}
