# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Conservar clases de configuración de Mapbox
-keep class com.mapbox.** { *; }
-keep interface com.mapbox.** { *; }

# Específicamente para el manejo de recursos de token
-keep class com.mapbox.maps.ResourceOptions { *; }


# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Mapbox
-keep class com.mapbox.** { *; }
-dontwarn com.mapbox.**



## Remove Log calls in release builds
#-assumenosideeffects class android.util.Log {
#    public static int v(...);
#    public static int d(...);
#    public static int i(...);
#    public static int w(...);
#    public static int e(...);
#}


# Reemplaza 'com.tu.paquete' por el nombre real de tu paquete
-keep class com.rogergcc.wiilrainprojectchallenguenasa.data.** { *; }
-keepattributes Signature, *Annotation*

# --- 1. CONFIGURACIÓN BASE (AutoValue, BouncyCastle, etc.) ---
-keep @com.google.auto.value.AutoValue class * { *; }
-keep @com.google.auto.value.AutoValue$Builder class * { *; }
-dontwarn com.google.auto.value.**

-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**

-keep class org.openjsse.** { *; }
-dontwarn org.openjsse.**

# --- 2. MAPBOX (Mapas y Geocoding) ---
# Mantiene el SDK de mapas y sus componentes nativos
-keep class com.mapbox.** { *; }
-dontwarn com.mapbox.**

# Específico para que GeocodingResponse no se rompa
-keep class com.mapbox.api.geocoding.** { *; }
-keep class com.mapbox.api.geocoding.v5.models.** { *; }
-keep class com.mapbox.geojson.** { *; }

# --- 3. GSON (Vital para que geocoding.enqueueCall funcione) ---
-keepattributes Signature, *Annotation*, EnclosingMethod
-keep class com.google.gson.** { *; }
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements com.google.gson.TypeAdapterFactory
-keep public class * implements com.google.gson.JsonSerializer
-keep public class * implements com.google.gson.JsonDeserializer

# Si tienes clases propias (POJOs) donde guardas datos de GSON, asegúralas:
# -keep class tu.paquete.modelos.** { *; }

# --- 4. ANDROIDX Y LIBRERÍAS DE UI ---
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class com.facebook.shimmer.** { *; }
-dontwarn com.facebook.shimmer.**

# --- 5. RETROFIT / OKHTTP (Usados por Mapbox internamente) ---
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keepattributes RuntimeVisiblePadding