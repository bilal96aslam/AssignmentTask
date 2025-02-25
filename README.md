MVVMTestSuite 🧪
A sample project demonstrating unit testing in an MVVM architecture using Room Database, Retrofit, and Repository pattern in Android.

🚀 Overview

This project demonstrates how to write unit tests for an MVVM-based Android application using Room Database, Retrofit API service, and Repository pattern. The tests cover:

ViewModel tests

Repository tests

Retrofit API service tests

Room Database tests

🏗 Project Structure

📂 assignmenttask  
├── 📂 app  
│   ├── MainActivity.kt  
│   ├── MyApplication.kt  
├── 📂 data  
│   ├── 📂 local/ (Local database using Room)  
│   ├── 📂 remote/ (Retrofit API services)  
├── 📂 di  
│   ├── AppModule.kt (Dependency Injection using Hilt)  
├── 📂 presentation  
│   ├── BookRepository.kt (Repository handling data operations)  
│   ├── BookScreen.kt (Jetpack Compose UI screen)  
│   ├── BookViewModel.kt (ViewModel for managing UI state)  
├── 📂 ui/ (UI-related components)  
├── 📂 res/ (Resources like drawables, layouts, etc.)  
├── build.gradle  
├── README.md  
└── … (Other project files)

🧪 Unit Tests

The unit tests validate different layers of the MVVM architecture:

ViewModel Tests: Ensuring correct ViewModel behavior.

Repository Tests: Mocking data sources and testing repository logic.

Retrofit API Tests: Mocking API responses.

Room Database Tests: Testing local database operations.

🛠 Tech Stack

Kotlin

MVVM Architecture

Hilt for Dependency Injection

Retrofit for API calls

Room for local database

JUnit & Mockito for Unit Testing

🏁 Getting Started

Clone the repository.

Open the project in Android Studio.

Run tests using:

./gradlew test

📌 License

This project is open-source and available under the MIT License.