import javax.net.ssl.HttpsURLConnection

rootProject.name = "pmdumper"

val forgeGradleVersion: String by settings
val kotlinVersion: String by settings

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("http://files.minecraftforge.net/maven") }
    }

    resolutionStrategy {
        eachPlugin {
            if(requested.id.id == "org.jetbrains.kotlin.jvm")
                useVersion(kotlinVersion)
            if(requested.id.id == "net.minecraftforge.gradle.forge")
                useModule("net.minecraftforge.gradle:ForgeGradle:$forgeGradleVersion")
        }
    }
}