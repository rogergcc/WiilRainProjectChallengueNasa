import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven(url = "https://jitpack.io")

        // Mapbox repository
//        maven {
//            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
//        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
//        maven(url = "https://jitpack.io")
        maven {
            url = uri("https://jitpack.io")
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password = findPropertyFromGradleFile("MAPBOX_STYLE_ACCESS_TOKEN") as String? ?: ""
            }
        }
    }
}
fun  findPropertyFromGradleFile(propertyKey: String): String? {
    val propertiesFile = file("${rootDir}/local.properties")
    if (propertiesFile.exists()) {
        val properties = Properties()
        properties.load(propertiesFile.inputStream())
        return properties.getProperty(propertyKey)
    }
    return null
}
rootProject.name = "WiilRainProjectChallengueNasa"
include(":app")
