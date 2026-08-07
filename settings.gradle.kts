pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        // Minecraft versions that get their own jar. 1.21.2, 1.21.3 and 1.21.9 are deliberately
        // absent: GeckoLib and/or Curios, both required dependencies, have no build for them.
        //
        // 1.21.5 is started but not finished, and a declared version that does not compile makes
        // every unqualified Gradle command fail, so it stays out until its port lands. Its
        // conditionals are already in the sources; adding it back here is the only step needed.
        versions("1.21.1", "1.21.10")
        vcsVersion = "1.21.1"
    }
}

rootProject.name = "EyPipesMod"
