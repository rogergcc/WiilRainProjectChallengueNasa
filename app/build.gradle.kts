import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
}
val properties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { properties.load(it) }
}
fun calculateVersionCode(versionName: String): Int {
    val clean = versionName.removePrefix("v")
    val parts = clean.split(".")
    if (parts.size != 3) return 1

    val major = parts[0].toIntOrNull() ?: 0
    val minor = parts[1].toIntOrNull() ?: 0
    val patch = parts[2].toIntOrNull() ?: 0

    return major * 10000 + minor * 100 + patch
}

val gitTag = System.getenv("GITHUB_REF_NAME") ?: "dev"
val computedVersionName = gitTag.removePrefix("v")
val computedVersionCode = calculateVersionCode(gitTag)


android {

    namespace = "com.rogergcc.wiilrainprojectchallenguenasa"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rogergcc.wiilrainprojectchallenguenasa"
        minSdk = 24
        targetSdk = 36
        versionName = computedVersionName
        versionCode = if (gitTag == "dev") 1 else computedVersionCode
        val defaultTokenMapbox = properties.getProperty("MAPBOX_DEFAULT_TOKEN", properties["MAPBOX_DOWNLOADS_TOKEN"] as String? ?: "")
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${properties["MAPBOX_STYLE_ACCESS_TOKEN"] ?: ""}\"")
        buildConfigField( "String", "MAPBOX_DEFAULT_TOKEN", "\"$defaultTokenMapbox\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // Fallback a variables de entorno o local.properties
            val keystorePath = System.getenv("KEYSTORE_PATH")
                ?: properties["KEYSTORE_PATH"]?.toString()
            val storePassword = System.getenv("KEY_STORE_PASSWORD")
                ?: properties["KEY_STORE_PASSWORD"]?.toString()
            val keyAlias = System.getenv("ALIAS")
                ?: properties["ALIAS"]?.toString()
            val keyPassword = System.getenv("KEY_PASSWORD")
                ?: properties["KEY_PASSWORD"]?.toString()

            // Solo asigna signingConfig si tenemos todos los datos
            if (keystorePath != null && storePassword != null && keyAlias != null && keyPassword != null) {
                storeFile = file(keystorePath)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // Si no están definidos, se deja vacío:
                // permite firmar manualmente desde Android Studio sin errores
                println("⚠️ Signing config release no configurada (manual signing disponible)")
            }
        }
    }
    buildTypes {
        getByName("debug") {
            isDebuggable = true
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material)
    implementation(libs.gson)
//    implementation(libs.mpndroidchart)
//    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.mapbox.maps)
    implementation(libs.mapbox.sdk.services)
    implementation(libs.mapbox.sdk.turf)
    implementation(libs.shimmer.facebook.android)
    implementation(libs.swiperefreshlayout)
//    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}