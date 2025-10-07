@echo off
echo ============================================
echo Dead Hand Android Project - First Time Setup
echo ============================================
echo.

echo [1/4] Setting up environment...
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot
set PATH=%PATH%;%JAVA_HOME%\bin

echo [2/4] Verifying Java installation...
java -version
if %ERRORLEVEL% neq 0 (
    echo ❌ ERROR: Java not found! Please run set-java-env.bat first
    pause
    exit /b 1
)

echo.
echo [3/4] Initializing Gradle wrapper...
if not exist "gradle\wrapper\gradle-wrapper.jar" (
    echo Downloading Gradle wrapper...
    mkdir gradle\wrapper 2>nul
    
    REM Use PowerShell to download the wrapper jar
    powershell -Command "try { Invoke-WebRequest -Uri 'https://github.com/gradle/gradle/raw/v8.0.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar' } catch { Write-Host 'Download failed, will generate wrapper instead' }"
)

echo.
echo [4/4] Testing Gradle...
gradlew.bat --version
if %ERRORLEVEL% neq 0 (
    echo ❌ Gradle setup failed. You may need to install Gradle manually.
    echo.
    echo Alternative: Install Android Studio which includes Gradle
    pause
    exit /b 1
)

echo.
echo ✅ SUCCESS! Dead Hand project is ready for development!
echo.
echo Next steps:
echo 1. To build APK: gradlew.bat assembleDebug
echo 2. To install on device: gradlew.bat installDebug
echo 3. Use VS Code tasks: Ctrl+Shift+P -> Tasks: Run Task
echo.
pause