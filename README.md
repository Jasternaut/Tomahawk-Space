# Tomahawk Space

**Tomahawk Space** is a modern Android application designed for space enthusiasts to explore the wonders of the universe. It leverages NASA's APIs to bring breathtaking space imagery and information directly to your fingertips

## Features

- **Astronomy Picture of the Day (APOD):** Discover a new celestial image or video every day, accompanied by professional explanations
- **Space Gallery:** Browse through a curated collection of high-quality space exploration photos
- **Secure Authentication:** Easy setup with your own NASA API key, securely stored using Android DataStore
- **Modern UI/UX:** Built entirely with Jetpack Compose and Material 3, featuring:
    - **Edge-to-Edge Experience:** Fully immersive interface that flows behind system bars
    - **Dynamic Dark Mode:** Seamlessly adapts to your system theme
    - **Fluid Animations:** Custom animated splash screen and smooth transitions
- **Modern Tech Stack:** Utilizes the latest Android development practices and libraries

## Technology Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **Navigation:** [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Data Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
- **Dependency Management:** Version Catalogs (libs.versions.toml)

## Getting Started

### Prerequisites

- **Android Studio**
- **JDK 17+**
- **Android SDK 35+** (Target SDK 37)
- **NASA API Key:** You can get a free key from [api.nasa.gov](https://api.nasa.gov/).

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/tomahawk-space.git
2. Open the project in Android Studio
3. Let Gradle sync and download dependencies
4. Build and run the app on an emulator or physical device (Min SDK 26)
