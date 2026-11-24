# Tomahawk Space

**Tomahawk Space** is a modern, cross-platform space exploration application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform** for **Android** and **Desktop (Windows, macOS, Linux)**. It leverages NASA's APIs to bring breathtaking space imagery, astronomical details, and celestial discoveries directly to your device.

---

## 🚀 Features

- **Astronomy Picture of the Day (APOD):** Discover daily celestial images or videos, accompanied by official explanations, high-resolution options, and date search.
- **Space Gallery:** Save favorite space photos to a local gallery with custom sorting (newest/oldest) and detailed views.
- **Adaptive Platform Navigation:**
  - **Mobile (Android):** Floating Navigation Dock with smooth active tab expansion and spring press feedback (toggleable to standard M3 Navigation Bar).
  - **Desktop (PC):** Collapsible side navigation drawer featuring square-shaped buttons with centered icons when collapsed, and expanded labeled menu items.
- **Secure Authentication & Preferences:** Seamless onboarding with NASA API key management (`DEMO_KEY` out of the box or custom key), securely stored via cross-platform DataStore.
- **Customizable Appearance:** Full support for Light, Dark, System, and Pure Black (AMOLED) modes, dynamic color schemes, and UI divider options.

---

## 🛠️ Technology Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Cross-Platform Engine:** Kotlin Multiplatform (KMP) & [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- **UI Framework:** Material 3 (Compose Multiplatform)
- **Networking:** [Ktor Client](https://ktor.io/) (Ktor CIO Engine, Content Negotiation, `kotlinx.serialization`)
- **Image Loading:** [Coil 3](https://coil-kt.github.io/coil/) (Coil Multiplatform Compose + Ktor 3 network fetcher)
- **Navigation:** [Jetpack Navigation Compose Multiplatform](https://developer.android.com/jetpack/compose/navigation)
- **Data Persistence:** [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore) (Multiplatform)
- **Asynchronous & Reactive:** Kotlin Coroutines & StateFlow
- **Dependency Management:** Gradle Version Catalogs (`gradle/libs.versions.toml`)

---

## 📁 Project Structure

```text
Tomahawk-Space/
├── shared/              # Core KMP module (UI, ViewModels, Repositories, Ktor API Service, DataStore)
│   ├── commonMain/      # Shared Compose UI screens, navigation, domain logic & resources
│   ├── androidMain/     # Android platform-specific implementations
│   └── jvmMain/         # Desktop JVM platform-specific implementations
├── androidApp/          # Android application entry point & Manifest
└── desktopApp/          # Desktop JVM application entry point (Windows/macOS/Linux)
```

---

## 🏁 Getting Started

### Prerequisites

- **Android Studio** (Ladybug / 2024.2.1+ or newer)
- **JDK 17+**
- **Android SDK 37** (Compile/Target: 37, Min SDK: 24)
- **NASA API Key:** Optional (uses `DEMO_KEY` by default). Free keys available at [api.nasa.gov](https://api.nasa.gov/).

### Installation & Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/tomahawk-space.git
   cd tomahawk-space
   ```

2. **Open in Android Studio:**
   - Open the cloned project folder and allow Gradle to sync dependencies.

3. **Run the Application:**
   - **Android:** Select the `androidApp` run configuration and run on an emulator or physical device.
   - **Desktop (PC):** Select the `desktopApp` run configuration or execute from terminal:
     ```bash
     ./gradlew :desktopApp:run
     ```
