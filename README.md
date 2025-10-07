# Dead Hand - Spy Themed Android App

A sophisticated spy-themed Android application built with Kotlin, featuring real-time data displays and a sleek dark interface.

## 🕵️ Features

### 🎯 Core Functionality
- **Spy-themed Authentication System**: Secure login with agent credentials
- **Real-time Clock Display**: Live time with seconds precision
- **Dynamic Date & Special Day Announcements**: Shows current date with special event notifications
- **Live Age Counter**: Real-time age calculation down to seconds
- **Personal Profile Integration**: Customized agent profile with actual photo

### 🎨 Design Elements
- **Dark Theme**: Professional spy aesthetic with dark backgrounds
- **Monospace Typography**: Terminal-style fonts for authentic look
- **Color Scheme**: Green (#00FF41), Cyan (#00D4FF), and Gold (#FFD700) accents
- **Gradient Borders**: Modern UI elements with styled containers
- **Clean Layout**: Single-page interface with organized sections

### ⚡ Real-time Features
- **Live Clock**: Updates every second (HH:MM:SS format)
- **Age Counter**: Shows Years, Months, Days, Hours, Minutes, Seconds
- **Special Day Detection**: Recognizes holidays and important dates
- **Dynamic Content**: All displays update automatically

### 🌍 Special Day Recognition
- Birthday celebrations (January 1st)
- Bangladesh Independence Day (March 26)
- Bangladesh Victory Day (December 16)
- International holidays (Christmas, Halloween, Valentine's Day)
- Weekend notifications
- Daily mission status

## 📱 Technical Specifications

### Development Environment
- **Platform**: Android Studio
- **Language**: Kotlin
- **Target SDK**: Android API Level compatible with modern devices
- **Device Tested**: Realme 8 (ColorOS)
- **Build System**: Gradle

### Architecture
- **Database**: SQLite for user data management
- **UI Framework**: Native Android LinearLayout construction
- **Image Handling**: Asset-based image loading system
- **Real-time Updates**: Handler-based threading for live data

### Key Components
- `MainActivity.kt`: Main application interface with integrated features
- Custom image integration from user assets
- Real-time calculation engines for age and time
- Special day detection algorithms

## 🚀 Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/GALIB-Dev/Dead-Hand.git
   cd Dead-Hand
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Install**:
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

## 📋 Requirements

- **Minimum Android Version**: API Level 21 (Android 5.0)
- **Target Device**: Modern Android smartphones
- **Storage**: Minimal space required
- **Permissions**: Basic app permissions only

## 👤 Profile Information

- **Agent Name**: Mohammad Al Galib
- **Born**: January 1, 2007
- **Location**: Bangladesh
- **Blood Type**: O+ (Universal Donor)
- **Status**: Active Agent with Top Secret Clearance

## 🛠️ Development History

- **Initial Development**: Complete spy theme transformation
- **Biometric Integration**: Removed for Realme 8 compatibility
- **Google Earth Features**: Removed per user preference
- **Profile Integration**: Added personal information display
- **Real-time Features**: Implemented live clock and age counter
- **UI Optimization**: Removed duplicate sections and cleaned interface
- **Image Integration**: Added actual profile photo support

## 🎯 Features Removed/Modified

- ❌ Biometric authentication (compatibility issues)
- ❌ Google Earth integration (complexity reduction)
- ❌ Separate profile pages (consolidated to main page)
- ❌ Emoji usage (clean text interface)
- ❌ Duplicate profile sections (streamlined display)

## 🔧 Build Configuration

The project uses Gradle build system with the following key configurations:
- Java 21 compilation target
- Modern Android SDK tools
- Optimized for Realme devices
- Lint checks disabled for faster builds

## 📞 Contact

**Developer**: Mohammad Al Galib (GALIB-Dev)  
**Email**: mohammadlgalib71@gmail.com  
**Location**: Bangladesh  

## 📄 License

This project is personal software developed for educational and demonstration purposes.

---

*"Mission Active - Stay Vigilant"* 🕵️‍♂️
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