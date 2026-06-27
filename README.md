🌩️ MeteoMarket

MeteoMarket is a weather-based stock market simulation game built natively for Android. Players can start a new career, invest in dynamic weather-based commodities, and grow their portfolio across in-game days.

This project demonstrates modern Android development best practices, featuring a completely declarative UI and robust local data persistence.

✨ Features

* Save File Management: Players can seamlessly manage up to 3 distinct save slots.

* Persistent State: Game progress (username, day count, and bank balance) is saved locally and securely.

* Market Dashboard: A scrollable, dynamic list of available weather markets with live investment actions.

* Theme Support: Fully supports dynamic system Light and Dark modes without losing navigation state.

🛠️ Tech Stack

This project is built using the modern Android development standard:

* Language: Kotlin (v2.4.0)

* UI Toolkit: Jetpack Compose (Material 3)

* Architecture: MVVM (Model-View-ViewModel) with Unidirectional Data Flow

* Local Database: Room (v2.6.1) utilizing KSP (v2.3.9) for annotation processing.

📂 Project Architecture

The codebase is organized by feature and layer to maintain a clean separation of concerns:

  
    app/src/main/java/com/yourapp/meteomarket/
    │
    ├── data/                       # Data Layer (Models & Local Storage)
    │   ├── models/                 # UI State Models (PlayerProfile, WeatherMarket)
    │   └── local/                  # Room Database, Entities (GameSaveSlot), and DAOs
    │
    ├── ui/                         # Presentation Layer (Jetpack Compose)
    │   ├── dashboard/              # Core gameplay loop and market listings
    │   ├── mainmenu/               # Save slot selection and creation
    │   └── theme/                  # Custom Material 3 Color and Typography definitions
    │
    ├── viewmodel/                  # Domain/Business Logic
    │   └── MarketViewModel.kt      # Manages game rules, investments, and state
    │
    └── MainActivity.kt             # Application entry point and Navigation host

🚀 Getting Started
Prerequisites

1. Android Studio: Latest version recommended (Ladybug or newer).
2. JDK: Java 17 or higher.

Installation
1. Clone the repository:
   (Bash) git clone https://github.com/symundy/MeteoMarket.git

3. Open the project in Android Studio.

4. Allow Gradle to sync. (Note: Ensure your libs.versions.toml or build.gradle.kts matches the Kotlin 2.4.0 and KSP 2.3.9 plugin requirements).

5. Build and run on an emulator or physical Android device.

🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
