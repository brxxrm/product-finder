# 📱 Product Finder - Detector de Productos con IA

Una aplicación Android moderna que utiliza **Machine Learning** para detectar productos en imágenes y facilitar su búsqueda en Google. Desarrollada con **Jetpack Compose** y **ML Kit**.

![Product Finder Demo](https://img.shields.io/badge/Android-Jetpack%20Compose-green)
![ML Kit](https://img.shields.io/badge/ML%20Kit-Image%20Labeling-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple)

## 🚀 **Características Principales**

### 🔍 **Detección Inteligente**
- **Reconocimiento automático** de productos usando ML Kit
- **Traducción en tiempo real** de inglés a español
- **Análisis de confianza** para cada producto detectado
- **Detección múltiple** hasta 5 productos por imagen

### 📸 **Captura Flexible**
- **Cámara integrada** para fotos en tiempo real
- **Galería de fotos** para imágenes existentes
- **Vista previa** de la imagen capturada
- **Gestión automática** de permisos

### 🔄 **Historial Dinámico**
- **Búsquedas recientes** que se actualizan automáticamente
- **Estado vacío** informativo cuando no hay historial
- **Acceso rápido** a búsquedas anteriores
- **Límite inteligente** de 6 búsquedas máximo

### 🌐 **Integración Web**
- **Búsqueda directa** en Google con el nombre del producto
- **Múltiples fallbacks** para garantizar compatibilidad
- **Resultados precisos** sin términos adicionales

## 🛠️ **Tecnologías Utilizadas**

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Kotlin** | 1.9.22 | Lenguaje principal |
| **Jetpack Compose** | 2024.02.00 | UI moderna y declarativa |
| **ML Kit** | 17.0.7 | Detección de productos |
| **ML Kit Translate** | 17.0.1 | Traducción automática |
| **CameraX** | 1.3.1 | Funcionalidad de cámara |
| **Coil** | 2.5.0 | Carga de imágenes |
| **Material 3** | Latest | Diseño Material Design |

## 📋 **Requisitos del Sistema**

- **Android 7.0** (API 24) o superior
- **Cámara** (opcional, para captura de fotos)
- **Almacenamiento** (para acceso a galería)
- **Internet** (para ML Kit y búsquedas)

## 🚀 **Instalación y Configuración**

### **1. Clonar el Repositorio**
\`\`\`bash
git clone https://github.com/BryanGranados/product-finder.git
cd product-finder
\`\`\`

### **2. Abrir en Android Studio**
- Abre **Android Studio**
- Selecciona **"Open an existing project"**
- Navega a la carpeta del proyecto
- Espera a que se sincronicen las dependencias

### **3. Configurar ML Kit**
El proyecto ya incluye las dependencias necesarias para ML Kit:
```kotlin
implementation("com.google.mlkit:image-labeling:17.0.7")
implementation("com.google.mlkit:translate:17.0.1")
