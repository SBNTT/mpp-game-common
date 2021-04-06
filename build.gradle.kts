@file:Suppress("UNUSED_VARIABLE")

repositories {
    mavenCentral()
}

plugins {
    kotlin("multiplatform")
    id("maven-publish")
}

val mavenRegistryName: String by project
val mavenRegistryUrl: String by project
val mavenRegistryUsernameEnvVariable: String by project
val mavenRegistryPasswordEnvVariable: String by project

val group: String by project
val version: String by project

project.group = group
project.version = version

kotlin {
    jvm()
    macosX64()
    mingwX64(); mingwX86()
    linuxX64(); linuxArm64(); linuxArm32Hfp(); linuxMips32(); linuxMipsel32()
    androidNativeArm64(); androidNativeArm32(); androidNativeX64(); androidNativeX86()
    iosArm64(); iosArm32(); iosX64()
    watchosArm64(); watchosArm32()
    tvosArm64(); tvosX64()
    js { browser(); nodejs() }
    wasm32()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = mavenRegistryName
            url = uri(mavenRegistryUrl)
            credentials {
                username = System.getenv(mavenRegistryUsernameEnvVariable)
                password = System.getenv(mavenRegistryPasswordEnvVariable)
            }
        }
    }
}


tasks {
    val macosHostTargets = arrayOf("ios", "tvos", "watchos", "macos")
    val linuxHostTargets = arrayOf("kotlinmultiplatform", "android", "linux", "wasm", "jvm", "js")
    val windowsHostTargets = arrayOf("mingw")

    val hostSpecificBuild by registering {
        dependsOn(when {
            isMacOsHost() -> tasksFiltering("compile", "", false, *macosHostTargets)
            isLinuxHost() -> tasksFiltering("compile", "", false, *linuxHostTargets)
            isWindowsHost() -> tasksFiltering("compile", "", false, *windowsHostTargets)
            else -> throw RuntimeException("Unsupported host")
        })
    }

    val hostSpecificTests by registering {
        dependsOn(when {
            isMacOsHost() -> tasksFiltering("", "", true, *macosHostTargets)
            isLinuxHost() -> tasksFiltering("", "", true, *linuxHostTargets)
            isWindowsHost() -> tasksFiltering("", "", true, *windowsHostTargets)
            else -> throw RuntimeException("Unsupported host")
        })
    }

    val hostSpecificPublish by registering {
        dependsOn(when {
            isMacOsHost() -> tasksFiltering("publish", "${mavenRegistryName}Repository", false, *macosHostTargets)
            isLinuxHost() -> tasksFiltering("publish", "${mavenRegistryName}Repository", false, *linuxHostTargets)
            isWindowsHost() -> tasksFiltering("publish", "${mavenRegistryName}Repository", false, *windowsHostTargets)
            else -> throw RuntimeException("Unsupported host")
        })
    }
}

fun isWindowsHost() = System.getProperty("os.name").startsWith("windows", ignoreCase = true)
fun isMacOsHost() = System.getProperty("os.name").startsWith("mac os", ignoreCase = true)
fun isLinuxHost() = System.getProperty("os.name").startsWith("linux", ignoreCase = true)

fun tasksFiltering(prefix: String, suffix: String, test: Boolean, vararg platforms: String) = tasks.names
        .asSequence()
        .filter { it.startsWith(prefix, ignoreCase = true) }
        .filter { it.endsWith(suffix, ignoreCase = true) }
        .filter { it.endsWith("test", ignoreCase = true) == test }
        .filter { it.contains("test", ignoreCase = true) == test }
        .filter { task -> platforms.any { task.contains(it, ignoreCase = true) } }
        .toMutableList()
