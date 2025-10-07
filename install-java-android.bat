@echo off
echo ====================================
echo Dead Hand Android Development Setup
echo ====================================
echo.

echo [1/4] Installing Chocolatey Package Manager...
echo.
powershell -Command "Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))"

echo.
echo [2/4] Installing Java JDK 17...
echo.
choco install openjdk17 -y

echo.
echo [3/4] Installing Android SDK Command Line Tools...
echo.
choco install android-sdk -y

echo.
echo [4/4] Setting up environment variables...
echo.
setx JAVA_HOME "C:\Program Files\OpenJDK\openjdk-17.0.2_8"
setx ANDROID_HOME "C:\Android\android-sdk"
setx PATH "%PATH%;%JAVA_HOME%\bin;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools"

echo.
echo ✅ Installation complete!
echo.
echo Next steps:
echo 1. Restart VS Code to load new environment variables
echo 2. Run: setup-android-sdk.bat to configure Android SDK
echo.
pause