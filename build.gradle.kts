@file:Suppress("UNUSED_VARIABLE")

import org.jfrog.gradle.plugin.artifactory.dsl.DoubleDelegateWrapper
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

repositories {
    mavenCentral()
}

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.jfrog.artifactory")
}

val glfwVersion: String by project
val vulkanVersion: String by project

val group: String by project
val version: String by project
val bintrayOrg: String by project
val bintrayRepo: String by project

project.group = group
project.version = version

artifactory {
    setContextUrl("https://sbntt.jfrog.io/artifactory")
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<DoubleDelegateWrapper> {
            setProperty("repoKey", "kmppge-common")
            setProperty("username", System.getenv("PUBLISHER_USERNAME"))
            setProperty("password", System.getenv("PUBLISHER_PASSWORD"))
        })
    })
}

tasks {
    val buildFromMacos by registering {
        tasksFiltering("compile", "", false, "ios", "tvos", "watchos", "macos").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val testFromMacos by registering {
        tasksFiltering("", "", true, "ios", "tvos", "watchos", "macos").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val publishFromMacos by registering {
        tasksFiltering("publish", "GitHubPackagesRepository", false, "ios", "tvos", "watchos", "macos").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val buildFromLinux by registering {
        (tasksFiltering("compile", "", false, "android", "linux", "wasm", "js") + "jsJar" + "jvmJar").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val testFromLinux by registering {
        tasksFiltering("", "", true, "android", "linux", "wasm", "js", "jvm").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val publishFromLinux by registering {
        tasksFiltering("publish", "GitHubPackagesRepository", false, "android", "linux", "wasm", "js", "jvm").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val buildFromWindows by registering {
        tasksFiltering("compile", "", false, "mingw").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val testFromWindows by registering {
        tasksFiltering("", "", true, "mingw").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }

    val publishFromWindows by registering {
        tasksFiltering("publish", "GitHubPackagesRepository", false, "mingw").forEach {
            dependsOn(this@tasks.getByName(it))
        }
    }
}

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
    }
}

fun tasksFiltering(prefix: String, suffix: String, test: Boolean, vararg platforms: String) = tasks.names
        .asSequence()
        .filter { it.startsWith(prefix, ignoreCase = true) }
        .filter { it.endsWith(suffix, ignoreCase = true) }
        .filter { it.endsWith("test", ignoreCase = true) == test }
        .filter { it.contains("test", ignoreCase = true) == test }
        .filter { task -> platforms.any { task.contains(it, ignoreCase = true) } }
        .toMutableList()
