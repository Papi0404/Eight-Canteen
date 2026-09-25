# R8 / ProGuard Optimization Rules for Eight Canteen App

# Retain Jetpack Compose runtime metadata
-keepclassmembers class * extends androidx.compose.runtime.RecomposeScopeImpl { *; }
-keepclassmembers class * extends androidx.compose.ui.node.LayoutNode { *; }

# Strip unnecessary debug logs and annotations in release APK
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Optimize classes and methods
-optimizationpasses 5
-allowaccessmodification
-dontwarn androidx.**
