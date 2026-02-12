// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.jetbrains.kotlin.android) apply false
//    alias(libs.plugins.kotlin.parcelize) apply false

    id("com.android.application") version "8.4.2" apply false
    kotlin("android") version "1.9.0" apply false
    kotlin("plugin.parcelize") version "1.9.0" apply false
//    para poder firmar mediante variables de entorno o local.properties
// por ejemplo:  ./gradlew assembleRelease
}