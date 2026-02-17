# Biocube - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Prerequisites
✅ Android Studio installed
✅ JDK 17 configured
✅ Android device or emulator ready

### Step 1: Open Project (30 seconds)
```
1. Launch Android Studio
2. Click "Open"
3. Select Biocube folder
4. Click OK
```

### Step 2: Sync Gradle (2-5 minutes)
```
- Wait for "Gradle sync" to complete
- If prompted, accept any SDK installations
- Check status bar for completion
```

### Step 3: Run App (1 minute)
```
1. Click green Run button (▶️)
2. Select device/emulator
3. Click OK
4. Wait for installation
```

### Step 4: Test the App (2 minutes)

**Login:**
- Username: `demo`
- Password: (anything)
- Click Login

**Explore:**
- ✅ Splash Screen (auto)
- ✅ Login Screen
- ✅ Profile Screen (first time)
- ✅ User Trainings Screen
- ✅ Navigation Menu
- ✅ Services Screen

## 📱 Quick Demo Credentials

```
User 1:
Username: demo
Password: (any)

User 2:
Username: john
Password: (any)

User 3:
Username: admin
Password: (any)
```

## ⚡ Features to Test

### 1. Authentication
- [x] Login with demo credentials
- [x] First-time profile setup
- [x] Biometric authentication (if device supports)

### 2. Navigation
- [x] Drawer menu (tap ☰ icon)
- [x] Navigate between screens
- [x] Logout and re-login

### 3. Biometric Scans
- [x] View 5 scan types
- [x] Face, Eye, Voice, Palm, Fingerprint
- [x] Tap to initiate scan

### 4. Services
- [x] e-Visa
- [x] Attendance (Check-In/Out)
- [x] Insurance
- [x] Banking

### 5. Background Features
- [x] Location tracking (30 min intervals)
- [x] Data sync (30 min intervals)
- [x] Offline mode support

## 🔧 Quick Fixes

### Problem: Gradle Sync Failed
**Fix:** File > Invalidate Caches / Restart

### Problem: App Won't Install
**Fix:** Build > Clean Project, then Rebuild

### Problem: Location Not Working
**Fix:** Grant location permission in device Settings

### Problem: Emulator Slow
**Fix:** Enable hardware acceleration in AVD settings

## 📚 Next Steps

1. **Explore Code:**
   - Check `presentation/` for UI screens
   - Check `data/` for database and API
   - Check `domain/` for business logic

2. **Customize:**
   - Edit theme in `presentation/theme/`
   - Modify screens in `presentation/[feature]/`
   - Add features following the architecture

3. **Learn More:**
   - Read README.md for full documentation
   - Read SETUP_GUIDE.md for detailed setup
   - Read PROJECT_STRUCTURE.md for architecture

## 📞 Quick Reference

### Important Files
```
Main Entry:           BiocubeApplication.kt
Main Activity:        MainActivity.kt
Navigation:           Navigation.kt
Theme:                Theme.kt
Database:             BiocubeDatabase.kt
API Mock:             MockApiService.kt
Mock Data:            assets/*.json
```

### Key Directories
```
Screens:              presentation/[feature]/[Feature]Screen.kt
ViewModels:           presentation/[feature]/[Feature]ViewModel.kt
Repositories:         data/repository/
Database:             data/local/
API:                  data/remote/
Models:               domain/model/
```

### Gradle Tasks
```
Clean:                ./gradlew clean
Build:                ./gradlew build
Install Debug:        ./gradlew installDebug
Run Tests:            ./gradlew test
```

## 🎯 Success Checklist

- [ ] Project opened in Android Studio
- [ ] Gradle sync completed successfully
- [ ] App runs on device/emulator
- [ ] Login screen appears
- [ ] Can login with demo credentials
- [ ] Profile screen works (first time)
- [ ] User Trainings screen displays
- [ ] Navigation drawer opens
- [ ] Services screen accessible
- [ ] All 5 biometric scans visible
- [ ] All 6+ services visible

## 🎉 You're Ready!

Congratulations! You now have a fully functional Biocube app running.

**What You Built:**
- ✅ Modern Jetpack Compose UI
- ✅ MVVM Clean Architecture
- ✅ Room Database
- ✅ Retrofit API Integration
- ✅ Dagger Hilt DI
- ✅ WorkManager Background Sync
- ✅ Location Tracking
- ✅ Biometric Authentication

**Time to Explore:**
- Modify screens
- Add new features
- Customize the theme
- Implement real APIs
- Add unit tests

---

**Need Help?**
- Check README.md
- Check SETUP_GUIDE.md
- Check PROJECT_STRUCTURE.md
- Review inline code comments

**Happy Coding! 🚀**
