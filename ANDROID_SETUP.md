# Dead Hand Android Development Setup Guide

## Option 1: Automated Installation (Requires Admin Rights)

1. **Right-click on `install-java-android.bat`** → **"Run as Administrator"**
2. Wait for installation to complete
3. Run `setup-android-sdk.bat` 
4. Restart VS Code

## Option 2: Manual Installation

### Step 1: Install Java JDK 17
1. **Download**: Go to [https://adoptium.net/temurin/releases/](https://adoptium.net/temurin/releases/)
2. **Select**: JDK 17 LTS for Windows x64
3. **Install**: Run the installer (use default settings)
4. **Verify**: Open new PowerShell and run `java -version`

### Step 2: Install Android Studio (Easiest)
1. **Download**: [https://developer.android.com/studio](https://developer.android.com/studio)
2. **Install**: Follow setup wizard
3. **SDK Location**: Note the SDK path (usually `C:\Users\USER\AppData\Local\Android\Sdk`)

### Step 3: Set Environment Variables
1. **Windows Key** → Type "Environment Variables" → **Edit system environment variables**
2. **Click "Environment Variables"**
3. **Add New System Variables**:
   - `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-17.0.x.x-hotspot`
   - `ANDROID_HOME` = `C:\Users\USER\AppData\Local\Android\Sdk`
4. **Edit PATH**: Add these to PATH:
   - `%JAVA_HOME%\bin`
   - `%ANDROID_HOME%\platform-tools`
   - `%ANDROID_HOME%\tools`

### Step 4: Verify Installation
Open new PowerShell and run:
```powershell
java -version
adb version
```

## Option 3: Portable Installation (No Admin Required)

### Java Portable
1. **Download**: OpenJDK 17 ZIP from [https://jdk.java.net/17/](https://jdk.java.net/17/)
2. **Extract**: To `C:\Tools\jdk-17` (create folder)
3. **Set JAVA_HOME**: `C:\Tools\jdk-17`

### Android SDK Portable
1. **Download**: Command line tools from [https://developer.android.com/studio#command-tools](https://developer.android.com/studio#command-tools)
2. **Extract**: To `C:\Tools\android-sdk`
3. **Set ANDROID_HOME**: `C:\Tools\android-sdk`

## Testing Your Setup

Once everything is installed, test in VS Code:

1. **Open Terminal in VS Code** (Ctrl + `)
2. **Run these commands**:
   ```powershell
   java -version           # Should show Java 17
   .\gradlew --version     # Should show Gradle info
   adb devices             # Should show "List of devices attached"
   ```

## Building Your Dead Hand App

Once setup is complete:

1. **Build APK**: `Ctrl+Shift+P` → "Tasks: Run Task" → "Build Debug APK"
2. **Install on Phone**: `Ctrl+Shift+P` → "Tasks: Run Task" → "Install APK on Device"
3. **Start Emulator**: `Ctrl+Shift+P` → "Tasks: Run Task" → "Start Android Emulator"

## Troubleshooting

- **"java not found"**: Restart VS Code after setting environment variables
- **"adb not found"**: Make sure Android SDK platform-tools is in PATH
- **Gradle errors**: Ensure JAVA_HOME points to JDK (not JRE)
- **Permission errors**: Enable USB debugging on your phone

Choose the option that works best for your system!