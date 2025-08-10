# 📱 Product Finder — Detector de Productos con IA

Detecta productos en imágenes usando Machine Learning y búscalos en Google al instante. Construido con ❤️ en Android usando Kotlin, Jetpack Compose y ML Kit.

[![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02.00-4285F4?logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![ML Kit](https://img.shields.io/badge/ML%20Kit-Image%20Labeling%2017.0.7-1E88E5)](https://developers.google.com/ml-kit)
[![ML Kit Translate](https://img.shields.io/badge/ML%20Kit-Translate%2017.0.1-1E88E5)](https://developers.google.com/ml-kit)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-A97BFF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Material 3](https://img.shields.io/badge/Material%203-Latest-000000?logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![License](https://img.shields.io/badge/License-MIT-black.svg)](#-licencia)
[![Stars](https://img.shields.io/github/stars/BryanGranados/product-finder?style=social)](https://github.com/BryanGranados/product-finder/stargazers)
[![Last Commit](https://img.shields.io/github/last-commit/BryanGranados/product-finder)](https://github.com/BryanGranados/product-finder/commits/main)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](#-contribuir)

---

## ✨ Demo

- 🎥 GIF de la app en funcionamiento
  - Coloca un GIF en `docs/demo/product-finder-demo.gif` y actualiza el enlace:
  - ![Demo](docs/demo/product-finder-demo.gif)

- 📸 Screenshots
  - Agrega tus capturas a `docs/screenshots/` y reemplaza las rutas:
    - Home: ![Home](docs/screenshots/home.png)
    - Resultados: ![Resultados](docs/screenshots/results.png)
    - Historial: ![Historial](docs/screenshots/history.png)

> Tip: para crear un GIF desde un video (`.mp4`) con ffmpeg:
```bash
ffmpeg -i screen-record.mp4 -vf "fps=15,scale=432:-1:flags=lanczos" -loop 0 docs/demo/product-finder-demo.gif
```

---

## 🧭 Tabla de Contenidos

- [Características](#--características)
- [Tecnologías](#-tecnologías-utilizadas)
- [Arquitectura](#-arquitectura)
- [Requisitos](#-requisitos-del-sistema)
- [Instalación](#-instalación-y-configuración)
- [Uso](#-uso)
- [Permisos](#-permisos)
- [Privacidad](#-privacidad)
- [Roadmap](#-roadmap)
- [Contribuir](#-contribuir)
- [Autor y Contacto](#-autor-y-contacto)
- [Licencia](#-licencia)

---

## 🚀  Características

### 🔍 Detección Inteligente
- Reconocimiento automático de productos con ML Kit
- Traducción en tiempo real (inglés → español)
- Análisis de confianza por etiqueta
- Detección múltiple (hasta 5 productos por imagen)

### 📸 Captura Flexible
- Cámara integrada (CameraX)
- Selección desde galería
- Vista previa de la imagen
- Gestión automática de permisos

### 🔄 Historial Dinámico
- Búsquedas recientes autogestionadas
- Estado vacío informativo
- Acceso rápido a búsquedas anteriores
- Límite inteligente de 6 elementos

### 🌐 Integración Web
- Búsqueda directa en Google con el nombre detectado
- Fallbacks para compatibilidad
- Resultados precisos sin términos adicionales

---

## 🛠 Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Kotlin | 1.9.22 | Lenguaje |
| Jetpack Compose | 2024.02.00 | UI moderna |
| ML Kit Image Labeling | 17.0.7 | Detección |
| ML Kit Translate | 17.0.1 | Traducción |
| CameraX | 1.3.1 | Cámara |
| Coil | 2.5.0 | Imágenes |
| Material 3 | Latest | Diseño |

Badges rápidos:
- ![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-A97BFF?logo=kotlin&logoColor=white)
- ![Compose](https://img.shields.io/badge/Compose-2024.02.00-4285F4?logo=android&logoColor=white)
- ![ML Kit](https://img.shields.io/badge/ML%20Kit-17.0.7-1E88E5)

---

## 🧩 Arquitectura

```mermaid title="Flujo de la App" type="diagram"
graph TD;
A["Abrir App"]B["Capturar foto o elegir de galería"];
BC["ML Kit: Image Labeling"];
CD["Top N etiquetas #40;max 5#41; con confianza"];
DE["ML Kit Translate EN #8594; ES"];
EF["Mostrar resultados en UI"];
FG["Guardar búsqueda en historial #40;max 6#41;"];
FH["Buscar en Google #40;intents / fallbacks#41;"];
```

---

## 📋 Requisitos del Sistema

- Android 7.0 (API 24) o superior
- Cámara (opcional, para captura)
- Almacenamiento (galería)
- Internet (descarga de modelos y búsquedas)

---

## 🚀 Instalación y Configuración

### 1) Clonar el repositorio
```bash
git clone https://github.com/BryanGranados/product-finder.git
cd product-finder
```

### 2) Abrir en Android Studio
- Abre Android Studio y selecciona `Open an existing project`
- Navega a la carpeta del proyecto
- Espera a que Gradle sincronice

### 3) Dependencias ML Kit (ya incluidas)
```kotlin
implementation("com.google.mlkit:image-labeling:17.0.7")
implementation("com.google.mlkit:translate:17.0.1")
```

> Sugerencia: descarga el modelo de traducción de forma proactiva al iniciar la app para que la primera traducción no tenga latencia extra.

---

## ▶️ Uso

1) Toca `Cámara` para tomar una foto o `Galería` para elegir una imagen.  
2) La app detecta productos y muestra etiquetas con su confianza.  
3) Traduce nombres al español automáticamente.  
4) Pulsa en una etiqueta para buscarla en Google.  
5) Revisa o reutiliza búsquedas en el Historial.

---

## 🔐 Permisos

En `AndroidManifest.xml` deberías declarar (según necesidades de tu implementación):

```xml
 Cámara 
<uses-permission android:name="android.permission.CAMERA" />

 Almacenamiento (si lees de galería en APIs antiguas) 
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
```

> CameraX maneja parte de la complejidad de permisos. Asegúrate de solicitar y explicar el uso al usuario.

---

## 🔏 Privacidad

- Los modelos de ML Kit pueden requerir descarga de componentes.  
- Las imágenes se procesan localmente para el etiquetado.  
- La búsqueda en Google abre el navegador con la consulta seleccionada.  
- No se suben imágenes a servidores de terceros desde la app (a menos que agregues esa funcionalidad).

Agrega un archivo `PRIVACY.md` con más detalles si planeas publicar en Play Store.

---

## 🗺 Roadmap

- [ ] Modo oscuro mejorado y tematización dinámica
- [ ] Exportar/limpiar historial
- [ ] Share Sheet: compartir resultados
- [ ] Multi-idioma configurable
- [ ] Ajustes: umbral de confianza y número de etiquetas
- [ ] Publicar APK en Releases y badge de descarga

---

## 🤝 Contribuir

¡Las PRs son bienvenidas!  
Pasos sugeridos:

1) Crea un issue describiendo tu propuesta  
2) Haz fork del repo y crea una rama: `feat/tu-feature`  
3) Añade tests o pruebas manuales si aplica  
4) Abre un Pull Request enlazando el issue

Plantillas útiles:
- `.github/ISSUE_TEMPLATE/bug_report.md`
- `.github/ISSUE_TEMPLATE/feature_request.md`
- `.github/pull_request_template.md`

---

## 🌟 Apoya el proyecto

- Dale ⭐ al repo: https://github.com/BryanGranados/product-finder  
- Compártelo en tus redes:  
  - LinkedIn (autor): https://www.linkedin.com/in/bryanarmando26/  
  - Compartir en LinkedIn: https://www.linkedin.com/sharing/share-offsite/?url=https%3A%2F%2Fgithub.com%2FBryanGranados%2Fproduct-finder  
  - Compartir en X/Twitter: https://twitter.com/intent/tweet?text=Probando%20Product%20Finder%20%F0%9F%93%B1%20Detecta%20productos%20con%20IA%20y%20b%C3%BAscalos%20en%20Google%20al%20instante.%20Repo%3A%20&url=https%3A%2F%2Fgithub.com%2FBryanGranados%2Fproduct-finder&hashtags=Android,JetpackCompose,MLKit,Kotlin

> Consejo: añade un Release con APK firmado (`app-release.apk`) y un badge de descarga:
> `[![Download](https://img.shields.io/badge/Download-APK-2ea44f)](https://github.com/BryanGranados/product-finder/releases)`

---

## 👤 Autor y Contacto

- Bryan Armando — Android Developer  
- LinkedIn: https://www.linkedin.com/in/bryanarmando26/  
- Portafolio / Web: agrega tu web si la tienes  
- Email: añade un correo si quieres recibir feedback

---

## ❓ FAQ

- \\"¿Funciona sin internet?\\"  
  - El etiquetado funciona localmente; la primera traducción puede requerir descargar el modelo. La búsqueda en Google requiere internet.

- \\"¿Por qué la primera traducción tarda?\\"  
  - Porque ML Kit debe descargar el modelo de idioma; después queda en caché.

- \\"¿Puedo aumentar el número de etiquetas?\\"  
  - Sí, ajusta el tope de resultados y/o el umbral de confianza.

---

## 🧱 Cómo funciona (resumen técnico)

1) Captura/selección de imagen con CameraX o galería  
2) Procesamiento con ML Kit Image Labeling  
3) Filtrado por confianza y recorte a top N  
4) Traducción con ML Kit Translate (EN → ES)  
5) Renderizado con Jetpack Compose + Material 3  
6) Historial en almacenamiento local (lista con límite de 6)

---

## 📦 Snippets útiles

Dependencias principales en Gradle:
```kotlin
implementation("com.google.mlkit:image-labeling:17.0.7")
implementation("com.google.mlkit:translate:17.0.1")
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("androidx.camera:camera-core:1.3.1")
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")
```

Descarga proactiva del modelo de traducción:
```kotlin
val options = TranslatorOptions.Builder()
    .setSourceLanguage(TranslateLanguage.ENGLISH)
    .setTargetLanguage(TranslateLanguage.SPANISH)
    .build()
val translator = Translation.getClient(options)
translator.downloadModelIfNeeded()
```

---

## 📝 Licencia

Este proyecto está bajo la licencia MIT. Consulta `LICENSE` para más información.

---

## ✅ Checklist para dejarlo \\"chulo\\"
- [ ] Añadir 3–5 screenshots en `docs/screenshots/`
- [ ] Subir un GIF corto (\<5MB) a `docs/demo/`
- [ ] Agregar badge de APK si publicas Releases
- [ ] Añadir `PRIVACY.md` y `SECURITY.md`
- [ ] Crear Issue/PR templates
- [ ] Publicar un post en LinkedIn y X con el GIF
- [ ] Poner ⭐ al repo y pedir feedback

\-\-\-

¿Otras ideas para mejorar?
- Tabla comparativa de velocidad/precisión con distintos umbrales
- Modo offline explicando claramente las limitaciones
- Atajos de accesibilidad (habla a texto / lector de pantalla)
- Tests instrumentados para el flujo principal
- Internationalization (i18n) y strings externalizados
- Compatibilidad con múltiples idiomas de origen (auto-detect)
```

