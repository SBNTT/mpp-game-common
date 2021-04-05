val name: String by settings
rootProject.name = name

pluginManagement {
    val kotlinVersion: String by settings
    val artifactoryVersion: String by settings

    plugins {
        kotlin("multiplatform") version kotlinVersion
        id("com.jfrog.artifactory") version artifactoryVersion
    }
}
