# Biocube - Android Biometric Authentication Application

A comprehensive Android application built with Jetpack Compose that demonstrates modern Android development practices including MVVM/Clean Architecture, Dagger Hilt, Room Database, Retrofit, WorkManager, and Biometric Authentication.

## 🚀 Features

### Core Features
- **Face Authentication** - TensorFlow Lite integration for on-device face recognition
- **Location Tracking** - Background location tracking using Fused Location Provider API
- **Data Synchronization** - Periodic sync every 30 minutes using WorkManager
- **Offline Mode** - Full offline support with Room Database
- **Biometric Scans** - Support for Face, Eye, Voice, Palm, and Fingerprint scans
- **Services** - Access to e-Visa, Attendance (Check-In/Check-Out), Insurance, Banking services

### Technical Features
- **Jetpack Compose** - Modern declarative UI
- **MVVM/Clean Architecture** - Separation of concerns with proper layering
- **Dagger Hilt** - Dependency injection
- **Kotlin Coroutines & Flow** - Asynchronous programming
- **Room Database** - Local data persistence
- **Retrofit** - REST API integration (Mock API using Assets)
- **WorkManager** - Background task scheduling
- **Biometric API** - Device biometric authentication

## 📱 App Flow

### New User Flow
1. **Splash Screen** → Shows for 2 seconds
2. **Login Screen** → User authentication
3. **Profile Screen** → Complete profile information
4. **User Trainings Screen** → Main screen with biometric scans

### Existing User Flow
1. **Splash Screen** → Shows for 2 seconds
2. **User Trainings Screen** → Directly to main screen

## 🏗 Project Structure

```
app/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── entity/           # Room Entities
│   │   └── BiocubeDatabase   # Room Database
│   ├── remote/
│   │   ├── api/              # API interfaces and mock service
│   │   └── dto/              # Data Transfer Objects
│   ├── repository/           # Repository implementations
│   ├── service/              # Services (Location, Biometric)
│   └── worker/               # WorkManager workers
├── domain/
│   ├── model/                # Domain models
│   └── repository/           # Repository interfaces
├── di/                       # Dependency Injection modules
├── presentation/
│   ├── login/                # Login screen
│   ├── profile/              # Profile screen
│   ├── services/             # Services screen
│   ├── splash/               # Splash screen
│   ├── usertrainings/        # User trainings screen
│   ├── navigation/           # Navigation setup
│   └── theme/                # Material 3 theme
└── util/                     # Utility classes
```

## 🔧 Technologies Used

### Android Stack
- **Kotlin** - Primary language
- **Jetpack Compose** - UI framework
- **Material 3** - Design system
- **Android SDK 24+** - Supports all modern Android versions

### Architecture Components
- **ViewModel** - UI state management
- **LiveData/StateFlow** - Observable data holders
- **Navigation Compose** - Type-safe navigation
- **Lifecycle** - Lifecycle-aware components

### Data & Networking
- **Room** - Local database (2.6.0)
- **Retrofit** - HTTP client (2.9.0)
- **Gson** - JSON parsing
- **OkHttp** - Network interceptors
- **DataStore** - Preferences storage

### Dependency Injection
- **Dagger Hilt** - Dependency injection (2.48)
- **Hilt WorkManager** - DI for Workers
- **Hilt Navigation Compose** - DI for ViewModels

### Background Processing
- **WorkManager** - Periodic background tasks (2.9.0)
- **Coroutines** - Asynchronous operations (1.7.3)

### Location & Biometric
- **Fused Location API** - Location tracking (21.0.1)
- **Biometric API** - Fingerprint/Face authentication (1.1.0)
- **TensorFlow Lite** - On-device ML for face recognition (2.14.0)

### Additional Libraries
- **Coil** - Image loading (2.5.0)
- **Core SplashScreen** - Splash screen API (1.0.1)

## 📦 Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Minimum Android API 24

### Installation

1. **Clone or extract the project**
```bash
cd Biocube
```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the Biocube folder
   - Click OK

3. **Sync Gradle**
   - Android Studio should automatically sync
   - If not, click "Sync Project with Gradle Files"

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the Run button (green triangle)
   - Select your device/emulator
   - Wait for installation and launch

### Mock Data Configuration

The app uses mock data from JSON files in the `assets` folder:

- **mock_users.json** - Demo users (username: "demo", "john", "admin")
- **mock_biometric_scans.json** - 5 biometric scan types
- **mock_services.json** - Available services

## 🔐 Authentication

### Demo Credentials
- Username: `demo`
- Password: (any password)

Other users: `john`, `admin`

### Biometric Authentication
The app supports device biometric authentication:
- Fingerprint
- Face recognition
- PIN/Pattern/Password (fallback)

## 🗄 Database Schema

### Users Table
- id (PK)
- username
- email
- fullName
- phoneNumber
- profileImageUrl
- isFirstLogin
- createdAt
- lastSyncedAt

### Locations Table
- id (PK)
- userId (FK)
- latitude
- longitude
- timestamp
- accuracy
- isSynced

## 🔄 Background Sync

The app uses WorkManager to sync location data every 30 minutes:

1. **Location Tracking**
   - Captures user location every 30 minutes
   - Stores in local database
   - Marks as unsynced

2. **Sync Worker**
   - Runs every 30 minutes
   - Uploads unsynced locations to server
   - Marks locations as synced
   - Requires network connectivity

3. **Data Retention**
   - Keeps synced locations for 7 days
   - Automatically deletes old data

## 📱 Screens Overview

### 1. Splash Screen
- Displays app logo
- Checks user authentication status
- Redirects to appropriate screen

### 2. Login Screen
- Username and password fields
- Biometric authentication option
- Demo credentials info

### 3. Profile Screen
- User information form
- Full name, email, phone
- Save and continue to trainings

### 4. User Trainings Screen
- Biocube banner
- Navigation drawer with menu
- List of 5 biometric scans
- Services button footer

#### Navigation Menu Items:
- Profile
- Services
- Home
- Logout
- Exit

### 5. Services Screen
- List of available services
- e-Visa
- Check In/Check Out
- Attendance
- Insurance
- Banking
- User Trainings button footer

## 🛠 Development Guide

### Adding New Features

1. **Domain Layer**
   - Add models in `domain/model/`
   - Add repository interface in `domain/repository/`

2. **Data Layer**
   - Add DTO in `data/remote/dto/`
   - Add Entity in `data/local/entity/`
   - Update DAO in `data/local/dao/`
   - Implement repository in `data/repository/`

3. **Presentation Layer**
   - Create ViewModel in `presentation/[feature]/`
   - Create Composable screen in `presentation/[feature]/`
   - Add navigation route in `presentation/navigation/`

### Dependency Injection

All dependencies are provided through Hilt modules in `di/AppModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideYourDependency(): YourClass {
        return YourClass()
    }
}
```

### Testing

The project structure supports:
- Unit tests for ViewModels
- Integration tests for Repositories
- UI tests for Composables

## 📝 Permissions

Required permissions in AndroidManifest.xml:

- `INTERNET` - Network access
- `ACCESS_FINE_LOCATION` - Precise location
- `ACCESS_COARSE_LOCATION` - Approximate location
- `ACCESS_BACKGROUND_LOCATION` - Background location
- `USE_BIOMETRIC` - Biometric authentication
- `CAMERA` - Camera access for face scan
- `POST_NOTIFICATIONS` - Notifications

## 🔒 Security Considerations

1. **API Keys** - Store securely (not in code)
2. **Biometric Data** - Never stored, only verified
3. **Location Data** - Encrypted in transit
4. **User Credentials** - Hashed and secured
5. **ProGuard** - Enabled for release builds

## 📚 Resources & Documentation

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Biometric Authentication](https://developer.android.com/training/sign-in/biometric-auth)
- [TensorFlow Lite](https://www.tensorflow.org/lite/android)

## 🐛 Troubleshooting

### Build Errors
1. Clean and rebuild: `Build > Clean Project` then `Build > Rebuild Project`
2. Invalidate caches: `File > Invalidate Caches / Restart`
3. Check Gradle sync

### Runtime Issues
1. Check permissions in device settings
2. Enable location services
3. Check network connectivity
4. Clear app data and restart

## 📄 License

This project is created for demonstration purposes.

## 👥 Support

For issues and questions, please check the inline code documentation.

---

**Built with ❤️ using Kotlin and Jetpack Compose**
