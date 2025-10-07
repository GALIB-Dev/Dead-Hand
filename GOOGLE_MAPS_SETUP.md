# Google Maps Integration Setup Guide

## 🗺️ **Satellite Surveillance Setup**

Your spy app now includes **Google Maps satellite surveillance** capabilities! To enable the satellite features:

### **Step 1: Get Google Maps API Key**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing project
3. Enable **Maps SDK for Android** API
4. Create credentials → API Key
5. Restrict the API key to Android apps (recommended)
6. Add your app's package name: `com.example.deadhand`

### **Step 2: Configure API Key**
1. Open `app/src/main/AndroidManifest.xml`
2. Find this line:
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_GOOGLE_MAPS_API_KEY_HERE" />
   ```
3. Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key

### **Step 3: Test Satellite Features**
- Launch the app and login with spy credentials
- Tap **🛰 SATELLITE SURVEILLANCE** button
- Grant location permissions when prompted
- Use the control panel to switch between satellite/hybrid views
- Track your location and view mission coordinates

## 🎯 **Spy Features Included**

### **Satellite Views**
- **🛰 SATELLITE VIEW**: Pure satellite imagery
- **🗺 HYBRID VIEW**: Satellite + road labels
- **📍 TRACK LOCATION**: Find agent's current position
- **🎯 MISSION POINTS**: Pre-loaded classified locations

### **Mission Coordinates**
- NYC HQ (CIA-style headquarters)
- London Station (MI6 inspired)
- Tokyo Outpost (International operations)
- Paris Safe House (European operations)
- Sydney Contact (Pacific operations)

### **Spy Interface Features**
- Terminal-style header with real-time timestamp
- Encrypted coordinate display
- Mission marker classification
- Location access security protocols

## ⚠️ **Security Notes**
- Location permissions required for agent tracking
- API key should be restricted to your app package
- All coordinates marked as "CLASSIFIED" for immersion

## 📱 **Usage**
1. Login to SHADOWNET with your agent credentials
2. Navigate to main dashboard
3. Tap "🛰 SATELLITE SURVEILLANCE" 
4. Grant location permissions
5. Use control buttons to explore satellite capabilities

**Experience true spy-level satellite surveillance!** 🕵️‍♂️🛰️