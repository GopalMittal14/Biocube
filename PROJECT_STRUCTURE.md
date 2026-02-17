# Biocube Project Structure

## Directory Tree

```
Biocube/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/
│   │   │   │   ├── mock_users.json
│   │   │   │   ├── mock_biometric_scans.json
│   │   │   │   └── mock_services.json
│   │   │   │
│   │   │   ├── java/com/biocube/app/
│   │   │   │   │
│   │   │   │   ├── BiocubeApplication.kt
│   │   │   │   │
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── UserDao.kt
│   │   │   │   │   │   │   └── LocationDao.kt
│   │   │   │   │   │   ├── entity/
│   │   │   │   │   │   │   ├── UserEntity.kt
│   │   │   │   │   │   │   └── LocationEntity.kt
│   │   │   │   │   │   └── BiocubeDatabase.kt
│   │   │   │   │   │
│   │   │   │   │   ├── remote/
│   │   │   │   │   │   ├── api/
│   │   │   │   │   │   │   ├── BiocubeApi.kt
│   │   │   │   │   │   │   └── MockApiService.kt
│   │   │   │   │   │   └── dto/
│   │   │   │   │   │       └── ApiModels.kt
│   │   │   │   │   │
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── UserRepository.kt
│   │   │   │   │   │   ├── LocationRepository.kt
│   │   │   │   │   │   ├── BiometricRepository.kt
│   │   │   │   │   │   └── ServiceRepository.kt
│   │   │   │   │   │
│   │   │   │   │   ├── service/
│   │   │   │   │   │   ├── LocationService.kt
│   │   │   │   │   │   └── BiometricAuthManager.kt
│   │   │   │   │   │
│   │   │   │   │   └── worker/
│   │   │   │   │       └── SyncWorker.kt
│   │   │   │   │
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/
│   │   │   │   │   │   ├── User.kt
│   │   │   │   │   │   ├── BiometricScan.kt
│   │   │   │   │   │   ├── Service.kt
│   │   │   │   │   │   └── LocationData.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── Repositories.kt
│   │   │   │   │
│   │   │   │   ├── di/
│   │   │   │   │   └── AppModule.kt
│   │   │   │   │
│   │   │   │   ├── presentation/
│   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   │
│   │   │   │   │   ├── splash/
│   │   │   │   │   │   └── SplashScreen.kt
│   │   │   │   │   │
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   └── LoginViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── profile/
│   │   │   │   │   │   ├── ProfileScreen.kt
│   │   │   │   │   │   └── ProfileViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── usertrainings/
│   │   │   │   │   │   ├── UserTrainingsScreen.kt
│   │   │   │   │   │   └── UserTrainingsViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── services/
│   │   │   │   │   │   ├── ServicesScreen.kt
│   │   │   │   │   │   └── ServicesViewModel.kt
│   │   │   │   │   │
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   └── Navigation.kt
│   │   │   │   │   │
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   │
│   │   │   │   └── util/
│   │   │   │       └── Resource.kt
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/ (for unit tests)
│   │
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── README.md
├── SETUP_GUIDE.md
└── PROJECT_STRUCTURE.md
```

## Layer Architecture

### Presentation Layer
**Location**: `presentation/`
**Responsibility**: UI and user interaction
**Components**:
- **Composable Functions**: UI screens
- **ViewModels**: UI state management
- **Navigation**: Screen navigation logic

### Domain Layer
**Location**: `domain/`
**Responsibility**: Business logic
**Components**:
- **Models**: Pure business entities
- **Repository Interfaces**: Contracts for data operations

### Data Layer
**Location**: `data/`
**Responsibility**: Data management
**Components**:
- **Local**: Room database, DAOs, Entities
- **Remote**: API services, DTOs
- **Repositories**: Implementation of domain interfaces
- **Services**: Location, Biometric services
- **Workers**: Background tasks

### Dependency Injection Layer
**Location**: `di/`
**Responsibility**: Dependency provision
**Components**:
- **Modules**: Hilt modules for DI

## Data Flow

```
User Interaction
      ↓
[Composable Screen]
      ↓
[ViewModel] ← observes → [StateFlow/LiveData]
      ↓
[Use Case / Repository Interface]
      ↓
[Repository Implementation]
      ↓
    ↙   ↘
[Local DB]  [Remote API]
 (Room)      (Retrofit)
```

## File Naming Conventions

### Kotlin Files
- **Screens**: `[Feature]Screen.kt` (e.g., LoginScreen.kt)
- **ViewModels**: `[Feature]ViewModel.kt` (e.g., LoginViewModel.kt)
- **Repositories**: `[Feature]Repository.kt` (e.g., UserRepository.kt)
- **DAOs**: `[Entity]Dao.kt` (e.g., UserDao.kt)
- **Entities**: `[Entity]Entity.kt` (e.g., UserEntity.kt)
- **Models**: `[Model].kt` (e.g., User.kt)

### Resource Files
- **Layouts**: Not used (Compose)
- **Drawables**: `ic_[name].xml`
- **Strings**: `strings.xml`
- **Themes**: `themes.xml`

## Module Dependencies

```
presentation
    ↓
  domain
    ↓
   data
```

- Presentation depends on Domain
- Data depends on Domain
- Domain has no dependencies (pure business logic)

## Key Technologies per Layer

### Presentation
- Jetpack Compose
- Material 3
- Navigation Compose
- Hilt Navigation Compose
- Coil (Image Loading)

### Domain
- Kotlin Coroutines
- Kotlin Flow

### Data
- Room Database
- Retrofit
- Gson
- OkHttp
- WorkManager
- Fused Location API
- Biometric API
- TensorFlow Lite

### DI
- Dagger Hilt
- Hilt WorkManager

## Design Patterns Used

1. **MVVM** (Model-View-ViewModel)
   - Views: Composables
   - ViewModels: State management
   - Models: Domain entities

2. **Repository Pattern**
   - Abstracts data sources
   - Single source of truth

3. **Dependency Injection**
   - Constructor injection
   - Hilt modules

4. **Observer Pattern**
   - StateFlow/Flow
   - LiveData (minimal use)

5. **Factory Pattern**
   - ViewModel factories (Hilt)

6. **Singleton Pattern**
   - Repositories
   - Services
   - Database

## Testing Structure

```
app/
├── src/
│   ├── test/           # Unit tests
│   │   └── java/com/biocube/app/
│   │       ├── domain/
│   │       ├── data/
│   │       └── presentation/
│   │
│   └── androidTest/    # Integration tests
│       └── java/com/biocube/app/
│           ├── data/
│           └── presentation/
```

## Build Variants

```
debug/
├── applicationIdSuffix: ".debug"
├── versionNameSuffix: "-DEBUG"
└── debuggable: true

release/
├── minifyEnabled: true
├── shrinkResources: true
└── proguardFiles
```

## Configuration Files

### Build Configuration
- `build.gradle.kts` (project)
- `build.gradle.kts` (app)
- `settings.gradle.kts`
- `gradle.properties`
- `proguard-rules.pro`

### Android Configuration
- `AndroidManifest.xml`
- Resource files (values/, xml/)

### Data Configuration
- Mock JSON files in assets/
- Database schema (Room)

## External Dependencies

### Network (18 total)
- Retrofit + Gson converter
- OkHttp + Logging interceptor

### Database (3 total)
- Room runtime
- Room KTX
- Room compiler (kapt)

### DI (5 total)
- Hilt Android
- Hilt Compiler (kapt)
- Hilt Navigation Compose
- Hilt Work
- Hilt Work Compiler (kapt)

### Jetpack (15+ total)
- Compose BOM
- Core KTX
- Lifecycle
- Navigation
- WorkManager
- Biometric
- SplashScreen
- DataStore

### ML (3 total)
- TensorFlow Lite
- TensorFlow Lite Support
- TensorFlow Lite GPU

### Others
- Coroutines
- Gson
- Coil
- Play Services Location

## Code Quality Tools

### Linting
- Android Lint (built-in)
- Kotlin Lint

### Code Analysis
- Android Studio Inspections
- Kotlin Compiler warnings

### Formatting
- Kotlin code style
- EditorConfig

## Version Control

Recommended `.gitignore`:
```
*.iml
.gradle
/local.properties
/.idea/
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties
```

---

**Last Updated**: February 2026
**Architecture**: MVVM + Clean Architecture
**Language**: Kotlin 1.9.20
**Min SDK**: 24 (Android 7.0)
**Target SDK**: 34 (Android 14)
