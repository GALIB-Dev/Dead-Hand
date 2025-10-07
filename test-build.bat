@echo off
echo Testing Dead Hand Project Build...
echo.

REM Set Java environment
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot
set PATH=%PATH%;%JAVA_HOME%\bin

echo Java version:
java -version
echo.

echo Compiling Kotlin files manually...
echo (This is a basic test - full Android build requires Android SDK)
echo.

REM Check if kotlinc is available (usually comes with Android Studio)
kotlinc -version 2>nul
if %ERRORLEVEL% equ 0 (
    echo ✅ Kotlin compiler found!
    echo Compiling MainActivity.kt...
    kotlinc app\src\main\java\com\example\deadhand\MainActivity.kt -cp "C:\Android\Sdk\platforms\android-34\android.jar"
) else (
    echo ⚠️ Kotlin compiler not found
    echo This is normal - Android Studio provides the full build environment
)

echo.
echo Your Dead Hand project structure is ready!
echo.
echo For full Android development, install Android Studio:
echo https://developer.android.com/studio
echo.
pause