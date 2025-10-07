@echo off
echo Building Dead Hand APK...
echo.

REM Navigate to project directory
cd /d "%~dp0"

REM Clean and build debug APK
echo Cleaning previous builds...
call gradlew.bat clean

echo Building debug APK...
call gradlew.bat assembleDebug

echo.
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✅ SUCCESS: APK built successfully!
    echo.
    echo APK Location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on your phone:
    echo 1. Enable "Unknown Sources" in Settings ^> Security
    echo 2. Copy APK to your phone
    echo 3. Open and install the APK
    echo.
    pause
) else (
    echo ❌ ERROR: APK build failed!
    pause
)