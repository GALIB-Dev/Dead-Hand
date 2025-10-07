@echo off
echo Setting up Java environment variables permanently...
echo.

echo Setting JAVA_HOME...
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot"

echo.
echo Adding Java to PATH...
setx PATH "%PATH%;C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot\bin"

echo.
echo ✅ Environment variables set!
echo.
echo Please:
echo 1. Restart VS Code to load new environment variables
echo 2. Or restart your terminal/PowerShell session
echo.
echo Then you can run: java -version
echo.
pause