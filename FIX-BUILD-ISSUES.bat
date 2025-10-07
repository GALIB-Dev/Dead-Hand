@echo off
echo =============================================
echo Dead Hand - Fix Build Configuration Issues
echo =============================================
echo.

echo ✅ FIXED: Updated build.gradle with stable versions:
echo - Android Gradle Plugin: 8.2.2 (stable)
echo - Kotlin: 1.9.10 (stable)
echo - Gradle: 8.8 (via wrapper)
echo.

echo 🔧 Next steps to fix the sync issue:
echo.

echo 1. STOP Gradle Daemon (clears cached errors):
echo    In Android Studio Terminal, run:
echo    gradlew --stop
echo.

echo 2. INVALIDATE CACHES:
echo    Android Studio → File → Invalidate Caches and Restart
echo    Choose "Invalidate and Restart"
echo.

echo 3. SET CORRECT JDK:
echo    File → Settings → Build Tools → Gradle
echo    Set Gradle JDK to: "Android Studio default JDK"
echo.

echo 4. SYNC PROJECT:
echo    Click "Sync Now" or File → Sync Project with Gradle Files
echo.

echo 🎯 Expected result: Project syncs successfully!
echo.

echo Alternative quick fix:
echo - Close Android Studio completely
echo - Delete .gradle folder in project root
echo - Reopen Android Studio and sync
echo.

pause