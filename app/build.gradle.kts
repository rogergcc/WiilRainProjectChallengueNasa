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

    buildTypes {
        release {
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