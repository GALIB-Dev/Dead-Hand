@echo off
echo ====================================
echo Android SDK Setup for Dead Hand
echo ====================================
echo.

echo [1/3] Accepting Android SDK licenses...
echo.
echo y | sdkmanager --licenses

echo.
echo [2/3] Installing required Android SDK components...
echo.
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" "system-images;android-34;google_apis;x86_64"

echo.
echo [3/3] Creating Android Virtual Device (AVD)...
echo.
echo no | avdmanager create avd -n "DeadHand_Emulator" -k "system-images;android-34;google_apis;x86_64" --force

echo.
echo ✅ Android SDK setup complete!
echo.
echo You can now:
echo 1. Build APK: .\gradlew assembleDebug
echo 2. Start emulator: emulator -avd DeadHand_Emulator
echo 3. Install on device: .\gradlew installDebug
echo.
pause