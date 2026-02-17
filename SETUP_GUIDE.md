# Biocube - Complete Setup Guide

## 📋 Table of Contents
1. [System Requirements](#system-requirements)
2. [Initial Setup](#initial-setup)
3. [Project Configuration](#project-configuration)
4. [Running the App](#running-the-app)
5. [Testing Features](#testing-features)
6. [Common Issues](#common-issues)

## System Requirements

### Development Environment
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux
- **RAM**: Minimum 8GB (16GB recommended)
- **Storage**: 10GB free space
- **Internet**: Required for Gradle dependencies

### Software Requirements
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Version 17 (bundled with Android Studio)
- **Android SDK**: API Level 34 (Android 14)
- **Gradle**: 8.2.0 (automatic via wrapper)

### Device Requirements
- **Minimum Android Version**: Android 7.0 (API 24)
- **Target Android Version**: Android 14 (API 34)
- **Recommended**: Android 10+ for best experience

## Initial Setup

### Step 1: Install Android Studio

1. Download Android Studio from [https://developer.android.com/studio](https://developer.android.com/studio)
2. Run the installer
3. Follow the setup wizard
4. Install recommended SDK components

### Step 2: Configure Android SDK

1. Open Android Studio
2. Go to `Tools > SDK Manager`
3. Install the following:
   - Android SDK Platform 34
   - Android SDK Build-Tools 34.0.0
   - Android Emulator
   - Google Play Services

### Step 3: Setup Android Emulator (Optional)

1. Go to `Tools > Device Manager`
2. Click `Create Device`
3. Select a device (e.g., Pixel 6)
4. Choose system image: Android 14 (API 34)
5. Click Finish

## Project Configuration

### Step 1: Open the Project

1. Launch Android Studio
2. Select `Open an Existing Project`
3. Navigate to the Biocube folder
4. Click `OK`

### Step 2: Gradle Sync

Android Studio will automatically start syncing. If not:
1. Click `File > Sync Project with Gradle Files`
2. Wait for sync to complete (may take 5-10 minutes first time)

### Step 3: Verify Configuration

Check the following files exist and are correct:

**build.gradle.kts (Project level)**
```kotlin
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
```

**build.gradle.kts (App level)**
```kotlin
android {
    namespace = "com.biocube.app"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.biocube.app"
        minSdk = 24
        targetSdk = 34
    }
}
```

### Step 4: Create Missing Directories (if needed)

Run in terminal:
```bash
mkdir -p app/src/main/assets
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi
```

## Running the App

### Option 1: Using Emulator

1. **Start Emulator**
   - Click the Device Manager icon
   - Select your emulator
   - Click the Play button
   - Wait for emulator to boot

2. **Run App**
   - Click the green Run button (▶️) in toolbar
   - Or press `Shift + F10` (Windows/Linux) or `Control + R` (macOS)
   - Select your emulator from the list
   - Click OK

3. **First Launch**
   - Gradle build may take 2-5 minutes
   - App will install on emulator
   - App will launch automatically

### Option 2: Using Physical Device

1. **Enable Developer Options**
   - Go to device Settings
   - About Phone
   - Tap Build Number 7 times
   - Developer Options enabled!

2. **Enable USB Debugging**
   - Settings > Developer Options
   - Enable USB Debugging
   - Connect device via USB

3. **Trust Computer**
   - When prompted on device
   - Click "Allow USB debugging"
   - Check "Always allow from this computer"

4. **Run App**
   - Click Run button in Android Studio
   - Select your device from list
   - Click OK

## Testing Features

### 1. Splash Screen
- App should show splash for 2 seconds
- Then navigate to Login (new user) or User Trainings (existing)

### 2. Login Flow

**Test Credentials:**
```
Username: demo
Password: (anything)

Other users: john, admin
```

**First Time Login:**
1. Enter credentials
2. Click Login
3. Should navigate to Profile Screen

**Returning User:**
1. Enter credentials
2. Click Login
3. Should navigate to User Trainings Screen

### 3. Profile Screen

1. Fill in:
   - Full Name: John Doe
   - Email: john@example.com
   - Phone: +1234567890

2. Click Continue
3. Should navigate to User Trainings

### 4. User Trainings Screen

**Navigation Drawer:**
1. Tap menu icon (☰)
2. Verify menu items:
   - User profile section
   - Profile
   - Services
   - Home
   - Logout
   - Exit

**Biometric Scans:**
1. Verify 5 scan cards displayed:
   - Face Scan
   - Eye Scan
   - Voice Scan
   - Palm Scan
   - Fingerprint

2. Tap any scan card
3. (Feature can be implemented to trigger biometric)

**Services Button:**
1. Scroll to bottom
2. Click "Services" button
3. Should navigate to Services Screen

### 5. Services Screen

**Services List:**
1. Verify services displayed:
   - e-Visa
   - Check In
   - Check Out
   - Attendance
   - Insurance
   - Banking

2. Tap any service
3. (Feature can be implemented)

**User Trainings Button:**
1. Click "User Trainings" button
2. Should return to User Trainings Screen

### 6. Location Tracking (Background)

**Grant Permissions:**
1. When prompted, allow:
   - Camera
   - Location (Always)
   - Notifications

**Verify Location Tracking:**
1. Open device Settings
2. Apps > Biocube > Permissions
3. Verify Location is "Allow all the time"

**Check Database:**
- Location data stored in Room database
- Viewable with Database Inspector:
  - `View > Tool Windows > App Inspection`
  - Select Database Inspector
  - View `locations` table

### 7. Data Sync (WorkManager)

**Verify WorkManager:**
1. In Android Studio:
   - `View > Tool Windows > Logcat`
2. Filter: `WorkManager`
3. Look for: "SyncWorker" execution logs
4. Should run every 30 minutes

**Manual Trigger (Debug):**
```kotlin
// In any screen, add a button:
Button(onClick = {
    val workRequest = OneTimeWorkRequestBuilder<SyncWorker>().build()
    WorkManager.getInstance(context).enqueue(workRequest)
}) {
    Text("Trigger Sync")
}
```

### 8. Biometric Authentication

**Prerequisites:**
- Device must have biometric hardware
- At least one fingerprint/face enrolled
- Or PIN/Pattern/Password set up

**Test Flow:**
1. Can be triggered from any scan
2. System biometric prompt appears
3. Authenticate with fingerprint/face
4. Or fallback to PIN/Pattern

## Common Issues

### Issue 1: Gradle Sync Failed

**Error**: "Could not download [dependency]"

**Solutions:**
1. Check internet connection
2. Try again: `File > Sync Project with Gradle Files`
3. Invalidate caches: `File > Invalidate Caches / Restart`
4. Check proxy settings if behind firewall

### Issue 2: Build Failed - Missing SDK

**Error**: "Failed to find target with hash string 'android-34'"

**Solution:**
1. Open SDK Manager
2. Install Android SDK Platform 34
3. Sync Gradle again

### Issue 3: App Won't Install on Device

**Error**: "Installation failed with message Failed to finalize session"

**Solutions:**
1. Uninstall old version from device
2. Clean project: `Build > Clean Project`
3. Rebuild: `Build > Rebuild Project`
4. Try again

### Issue 4: Location Not Updating

**Check:**
1. Location permission granted?
2. Location services enabled on device?
3. GPS signal available? (Move outdoors)
4. Check Logcat for errors

**Fix:**
1. Go to device Settings
2. Location > App permissions
3. Biocube > Allow all the time

### Issue 5: Database Inspector Empty

**Reason:** Database created on first use

**Solution:**
1. Login at least once
2. Navigate through screens
3. Refresh Database Inspector
4. Data should appear

### Issue 6: WorkManager Not Running

**Debug Steps:**
1. Check Logcat for WorkManager logs
2. Verify constraints met (network required)
3. Check battery optimization settings
4. Ensure app not force-stopped

**Manual Check:**
```kotlin
// Add this to a ViewModel
WorkManager.getInstance(context)
    .getWorkInfosForUniqueWork(SyncWorker.WORK_NAME)
    .get()
    .forEach { workInfo ->
        Log.d("WorkManager", "State: ${workInfo.state}")
    }
```

### Issue 7: Assets Not Found

**Error**: "FileNotFoundException: mock_users.json"

**Solution:**
1. Verify files exist in `app/src/main/assets/`
2. Clean project
3. Rebuild
4. If still missing, manually create:

```bash
cd app/src/main/
mkdir -p assets
# Copy JSON files to assets folder
```

### Issue 8: Hilt Errors

**Error**: "Hilt component not generated"

**Solutions:**
1. Ensure `@HiltAndroidApp` on Application class
2. Ensure `@AndroidEntryPoint` on Activities
3. Clean and rebuild
4. Check kapt configuration in build.gradle

### Issue 9: Compose Preview Not Working

**Error**: Preview shows "Failed to instantiate"

**Solutions:**
1. Make sure preview functions are annotated with `@Preview`
2. Functions should not require parameters
3. Use `@PreviewParameter` for data
4. Build project first

### Issue 10: Memory Issues

**Error**: OutOfMemoryError during build

**Solution:**
Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## Advanced Configuration

### Enable R8 (Code Shrinking)

For release builds, edit `app/build.gradle.kts`:
```kotlin
buildTypes {
    release {
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

### Configure Signing (Release)

Create keystore:
```bash
keytool -genkey -v -keystore biocube.jks -keyalg RSA -keysize 2048 -validity 10000 -alias biocube
```

Add to `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("biocube.jks")
            storePassword = "your-password"
            keyAlias = "biocube"
            keyPassword = "your-password"
        }
    }
}
```

## Performance Optimization

### Reduce Build Time
1. Enable Gradle daemon
2. Use parallel builds
3. Configure build cache

Edit `gradle.properties`:
```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

### Reduce APK Size
1. Enable R8
2. Use vector drawables
3. Remove unused resources
4. Enable APK splitting

## Debugging Tips

### Enable Verbose Logging

In any ViewModel or Repository:
```kotlin
companion object {
    private const val TAG = "YourClassName"
}

Log.d(TAG, "Your debug message")
Log.e(TAG, "Error message", exception)
```

### View Database

Use Database Inspector:
1. Run app on device/emulator
2. `View > Tool Windows > App Inspection`
3. Database Inspector tab
4. Select running app
5. Browse tables

### Network Monitoring

Use Network Profiler:
1. `View > Tool Windows > Profiler`
2. Select app process
3. Click Network
4. Monitor API calls

## Next Steps

After successful setup:
1. Explore the codebase
2. Customize UI theme
3. Add new features
4. Implement real API endpoints
5. Add unit tests
6. Configure CI/CD

## Support

For additional help:
- Check Android Studio documentation
- Review Jetpack Compose samples
- Explore Dagger Hilt guides
- Read official Android developer docs

---

**Happy Coding! 🚀**
