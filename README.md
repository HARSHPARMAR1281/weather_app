Weather App

Project Overview:
A modern Android weather application built with Kotlin that provides real-time weather information using the OpenWeatherMap API. The app follows MVVM architecture pattern and implements best practices for Android development.

Features Implemented:

Core Features
- 🌤️ Real-time weather updates
- 📍 Location-based weather tracking
- 🔍 City search functionality
- 💾 Local data persistence
- 👤 User authentication
- 📱 Responsive UI with weather animations
- 🌍 Support for multiple locations
- 📊 Weather history tracking

Technical Features
- MVVM Architecture
- Kotlin Coroutines for asynchronous operations
- LiveData for reactive programming
- Retrofit for API integration
- MongoDB Realm for local storage
- Firebase Authentication
- Material Design components
- Lottie animations for weather conditions
- Location services integration
- Error handling and loading states

Setup Instructions:

Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK 21 or higher
- Google Play Services
- Internet connection
- Location services enabled (for location-based features)

API Keys Required
1. OpenWeatherMap API Key
   - Sign up at [OpenWeatherMap](https://openweathermap.org/api)
   - Get your API key
   - Add to `local.properties`:
     ```properties
     OPENWEATHER_API_KEY=your_api_key_here
     ```

2. Firebase Configuration
   - Create a Firebase project
   - Add your `google-services.json` to the app directory
   - Enable Authentication in Firebase Console

MongoDB Setup
1. Create a MongoDB Atlas account
2. Create a new cluster
3. Get your connection string
4. Update the connection details in `Constants.kt`

Installation Steps
1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/weather_app.git
   ```

2. Open the project in Android Studio

3. Sync project with Gradle files

4. Update API keys in `local.properties`

5. Build and run the application

Gradle Dependencies
The project uses the following main dependencies:
```gradle
dependencies {
    // AndroidX
    implementation 'androidx.core:core-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.5.0'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.5.1'

    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'

    // Firebase
    implementation platform('com.google.firebase:firebase-bom:30.3.1')
    implementation 'com.google.firebase:firebase-auth-ktx'

    // MongoDB Realm
    implementation 'io.realm.kotlin:library-base:1.4.0'
    implementation 'io.realm.kotlin:library-sync:1.4.0'

    // Lottie
    implementation 'com.airbnb.android:lottie:5.2.0'

    // Location Services
    implementation 'com.google.android.gms:play-services-location:20.0.0'
}
```

Project Structure:
pp/
├── src/
│ ├── main/
│ │ ├── java/com/example/weather_app/
│ │ │ ├── activities/ # UI components
│ │ │ ├── models/ # Data models
│ │ │ ├── network/ # API services
│ │ │ ├── utils/ # Utility classes
│ │ │ ├── viewmodel/ # ViewModels
│ │ │ └── WeatherApplication.kt
│ │ ├── res/ # Resources
│ │ └── AndroidManifest.xml
│ └── test/ # Unit tests
└── build.gradle

Features in Detail:

Weather Information
- Current temperature
- Feels like temperature
- Humidity
- Wind speed
- Weather description
- Weather condition icons
- City and country information

Location Features
- GPS-based location detection
- Manual city search
- Location permission handling
- Background location updates

Data Management
- Local caching of weather data
- User-specific weather history
- City-based weather history
- Automatic data updates

User Interface
- Material Design components
- Dynamic weather backgrounds
- Weather condition animations
- Loading indicators
- Error handling UI
- Responsive layouts

Security
- Firebase Authentication
- Secure API key storage
- Encrypted local storage
- Permission management

Contributing:
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

Acknowledgments:
- OpenWeatherMap for weather data API
- Firebase for authentication
- MongoDB for database services
- Android Jetpack libraries
- Lottie for animations
Screenshots/Images:
1. https://github.com/user-attachments/assets/93e8b07b-de6c-4098-8291-66ed69814aaf
2. https://github.com/user-attachments/assets/633fc68f-81f4-463c-ae45-5d7014222725
3. https://github.com/user-attachments/assets/8d3cfd29-5d29-41fe-99e9-bfc7a55a2560

4. https://github.com/user-attachments/assets/83f3d78e-e1a6-448e-815b-109d039917c9
5. https://github.com/user-attachments/assets/048f35f1-492b-48b6-915d-8fba34541e46





