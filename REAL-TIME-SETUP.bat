@echo off
echo ===================================================
echo Dead Hand - Android SDK Manual Installation Guide
echo ===================================================
echo.

echo What you need to view your app in real-time:
echo.
echo [1] Android SDK Command Line Tools
echo     Download from: https://developer.android.com/studio#command-tools
echo     Extract to: C:\Android\cmdline-tools\latest\
echo.
echo [2] Install SDK Components
echo     Run: sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
echo.
echo [3] Set Environment Variables
echo     ANDROID_HOME = C:\Android
echo     PATH += %%ANDROID_HOME%%\platform-tools;%%ANDROID_HOME%%\cmdline-tools\latest\bin
echo.
echo [4] Enable USB Debugging on Your Phone
echo     Settings → About Phone → Tap Build Number 7 times
echo     Settings → Developer Options → USB Debugging (ON)
echo.
echo [5] Connect Phone and Test
echo     adb devices
echo.
echo [6] Build and Install APK
echo     gradlew assembleDebug
echo     adb install app\build\outputs\apk\debug\app-debug.apk
echo.
echo ===================================================
echo EASIER OPTION: Install Android Studio
echo It includes everything above automatically!
echo https://developer.android.com/studio
echo ===================================================
echo.
pause