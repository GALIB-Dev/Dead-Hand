# Dead Hand Android Application

A secure Android application built with Kotlin and modern Android development practices.

## Project Overview

**Dead Hand** is a security-focused Android application with a dark, military-inspired interface design. The app demonstrates modern Android development patterns while maintaining a theme appropriate for a security or monitoring system.

## Technical Specifications

- **Language**: Kotlin
- **Package**: `com.example.deadhand`
- **Build System**: Gradle 8.0
- **Min SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)
- **UI Framework**: Material Design 3 (Dark Theme)
- **Architecture**: MVVM with Navigation Component

## Features

✅ **Security-Themed UI**: Dark theme with red/orange accent colors  
✅ **Material Design 3**: Modern Android UI components  
✅ **Navigation Component**: Fragment-based navigation  
✅ **View Binding**: Type-safe view references  
✅ **Permissions**: SMS, Internet, Network access ready  
✅ **Security Configuration**: Obfuscated release builds  

## Project Structure

```
Dead-Hand/
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── java/com/example/deadhand/
│       │   ├── MainActivity.kt
│       │   ├── FirstFragment.kt
│       │   └── SecondFragment.kt
│       ├── res/
│       │   ├── layout/
│       │   ├── values/
│       │   ├── navigation/
│       │   └── xml/
│       └── AndroidManifest.xml
├── gradle/wrapper/
├── build.gradle
└── settings.gradle
```

## Getting Started

### Prerequisites
- **Android Studio** (latest version)
- **Android SDK API 34**
- **Kotlin plugin** enabled
- **Gradle 8.0+**

### Installation
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `Dead-Hand` folder
4. Click "Open"
5. Wait for Gradle sync to complete
6. Run the app on device/emulator

### Build Commands
```bash
# Clean and build
./gradlew clean build

# Debug APK
./gradlew assembleDebug

# Release APK (obfuscated)
./gradlew assembleRelease

# Run tests
./gradlew test
```

## App Functionality

### Main Screen (FirstFragment)
- **Title**: "DEAD HAND SYSTEM"  
- **Status**: System status display  
- **Action**: Configure system button  

### Configuration Screen (SecondFragment)
- **Warning**: Authorized personnel only  
- **Navigation**: Back to main screen  

### Security Features
- **Dark Theme**: Military/security aesthetic
- **Permissions**: SMS, network access configured
- **Obfuscation**: ProGuard rules for release builds
- **Data Protection**: Backup exclusion rules

## Customization

### Theme Colors
Edit `app/src/main/res/values/colors.xml`:
- `accent_red`: Primary danger color
- `accent_orange`: Warning color  
- `background_dark`: Main background

### Text Content
Edit `app/src/main/res/values/strings.xml`:
- `app_name`: Application name
- `dead_hand_title`: Main screen title
- System messages and warnings

### Add New Features
1. Create new Fragment classes in `java/com/example/deadhand/`
2. Add corresponding layout XML in `res/layout/`
3. Update navigation graph in `res/navigation/nav_graph.xml`

## Next Development Steps

Consider implementing:
- **Authentication**: PIN/biometric security
- **Network Communication**: Secure server integration  
- **Database**: Local data storage (Room)
- **Encryption**: Data security layers
- **Background Services**: System monitoring
- **Push Notifications**: Alert system
- **Real-time Updates**: WebSocket/Socket.IO integration

## Security Considerations

- All sensitive data excluded from backups
- ProGuard obfuscation enabled for releases
- Logging disabled in production builds
- Secure permissions model implemented

## License

This project is developed for educational and demonstration purposes.