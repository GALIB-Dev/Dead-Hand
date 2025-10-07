# Dead Hand - Android Studio Setup Checklist

## ✅ **After Android Studio Installation:**

### 1. **First Launch Setup**
- [ ] Open Android Studio
- [ ] Complete the setup wizard
- [ ] Install recommended SDK components (API 34, latest build tools)
- [ ] Note the SDK location (usually: `C:\Users\USER\AppData\Local\Android\Sdk`)

### 2. **Open Your Dead Hand Project**
- [ ] File → Open → Select: `C:\Users\USER\Downloads\NEW project\Dead-Hand`
- [ ] Wait for Gradle sync to complete
- [ ] Fix any dependency issues (Android Studio will prompt)

### 3. **Verify Project Structure**
- [ ] Check `app/src/main/java/com/example/deadhand/` contains:
  - [ ] MainActivity.kt
  - [ ] FirstFragment.kt  
  - [ ] SecondFragment.kt
- [ ] Check `app/src/main/res/layout/` contains layout files
- [ ] Verify `app/build.gradle` has correct dependencies

### 4. **Test Build**
- [ ] Click **Build** → **Make Project** (Ctrl+F9)
- [ ] Fix any compilation errors
- [ ] Successful build = Ready for phone deployment!

## 📱 **Phone Setup (Do This Now)**

### Enable Developer Options:
1. **Settings** → **About Phone**
2. **Tap "Build Number" 7 times** (until you see "You are now a developer!")
3. **Go back** to main Settings
4. **Developer Options** should now be visible

### Enable USB Debugging:
1. **Settings** → **Developer Options**
2. **USB Debugging** → Turn **ON**
3. **Install via USB** → Turn **ON** (if available)
4. **Stay Awake** → Turn **ON** (helpful for development)

### Connect Your Phone:
1. **Connect USB cable** to computer
2. **Select "File Transfer" or "MTP"** on phone
3. **Allow USB Debugging** when prompted
4. **Check "Always allow from this computer"**

## 🚀 **Deploy Dead Hand App**

### In Android Studio:
1. **Click the "Run" button** (▶️) or press **Shift+F10**
2. **Select your phone** from device list
3. **Click OK** - app will build and install automatically!

### Alternative - Manual Install:
1. **Build APK**: `.\gradlew assembleDebug`
2. **Find APK**: `app\build\outputs\apk\debug\app-debug.apk`
3. **Install**: `adb install app-debug.apk`

---

## 🎯 **Expected Result:**

Your **Dead Hand** app should appear on your phone with:
- **Dark military-style interface**
- **"DEAD HAND SYSTEM" title**
- **"System Status: STANDBY" message**  
- **Red "Configure System" button**
- **Navigation between main and config screens**

---

## 🔍 **Troubleshooting:**

**"Device not detected":**
- Ensure USB debugging is enabled
- Try different USB cable/port
- Install phone drivers if needed

**"Build failed":**
- Check Gradle sync completed
- Verify Java/Kotlin versions
- Update dependencies in Android Studio

**"App crashes":**
- Check Logcat in Android Studio
- Verify all resource files exist
- Test on Android emulator first

---

**🎉 Once working, you'll have real-time development:**
- **Edit code** → **Run** → **Instantly see changes on phone!**