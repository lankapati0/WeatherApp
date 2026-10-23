# ☀️ Weather App

A modern, production-ready Android application built to provide real-time weather forecasts. This project demonstrates clean architecture principles, modern Android development best practices, and robust dependency injection.

---

## 🚀 Tech Stack & Architecture

This app is built using the latest Android tech stack and follows the **MVVM (Model-View-ViewModel)** architectural pattern.

* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel) with unidirectional data flow (UDF)
* **Dependency Injection:** 
  * Hilt (Primary DI)
* **Networking:** with OkHttp for REST API consumption
* **Local Database:** Room for offline data persistence and caching
* **Concurrency:** Kotlin Coroutines & Flow for asynchronous operations

---

## 📱 Features

* **Real-Time Weather:** Fetch current weather conditions, temperature, humidity, and wind speed for any location.
* **Forecasts:** Hourly and multi-day weather predictions.
* **Offline Support:** Seamlessly view previously searched weather data cached locally via Room persistence.
* **Robust Dependency Injection:** Modularized and testable codebase powered by Hilt and Dagger.

---

## 🛠️ Project Structure

com.example.weatherapp
│
├── data                  # Data Layer
│   ├── local             # Room Database, entities, and DAOs
│   ├── remote            # Retrofit API services and DTOs
│   └── repository        # Repository implementations
│
├── di                    # Dependency Injection modules (Hilt / Dagger)
│
├── ui                    # Presentation Layer
│   ├── main              # MainActivity and UI components
│   └── weather           # Weather ViewModel, fragments/screens, and adapters
│
└── utils                 # Helper classes, extensions, and constants

⚙️ Getting Started
Prerequisites
Android Hedgehog or newer (Android Studio)

JDK 17 or higher

An API key from a weather provider Open-Meteo

Installation & Setup
Clone the repository:
git clone [https://github.com/lankapati0/weather-app.git]

🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

📄 License
This project is distributed under the MIT License. See LICENSE for more information.
