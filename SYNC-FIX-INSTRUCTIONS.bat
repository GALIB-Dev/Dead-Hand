@echo off
echo =============================================
echo Dead Hand - Android Studio Sync Fix
echo =============================================
echo.

echo ✅ FIXED: Updated project configuration for compatibility
echo.
echo Changes made:
echo - Commented out Java 25 path in gradle.properties
echo - Updated Android Gradle Plugin to 8.1.4 (compatible with Gradle 8.5)
echo - Changed Java compatibility to 1.8 (most compatible)
echo - Added Gradle optimizations
echo.
echo NEXT STEPS:
echo.
echo 1. In Android Studio:
echo    File → Settings → Build, Execution, Deployment → Build Tools → Gradle
echo    Set "Gradle JDK" to "Android Studio's bundled JDK"
echo.
echo 2. Click "Sync Now" in Android Studio
echo.
echo 3. If still having issues, try:
echo    File → Invalidate Caches and Restart
echo.
echo 4. Alternative: Let Android Studio auto-configure
echo    File → Project Structure → Project → Gradle JDK
echo    Select "Android Studio's bundled JDK"
echo.
echo =============================================
echo Your Dead Hand app should now sync properly!
echo =============================================
pause