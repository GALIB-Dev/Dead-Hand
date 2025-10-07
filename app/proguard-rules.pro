# Add project specific ProGuard rules here.
# Dead Hand System - Security-focused obfuscation rules

# Keep main application classes
-keep class com.example.deadhand.** { *; }

# Security: Obfuscate sensitive method names
-obfuscatedictionary proguard-dictionary.txt

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Keep WebView JavaScript interfaces
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Security: Hide stack traces in release
-keepattributes !SourceFile,!LineNumberTable