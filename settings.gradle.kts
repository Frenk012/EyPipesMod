pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
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
        // Each node is "<minecraft>-<loader>" and builds with build.<loader>.gradle.kts.
        fun match(version: String, vararg loaders: String) {
            for (loader in loaders) version("$version-$loader", version).buildscript("build.$loader.gradle.kts")
        }

        // The most played version on each loader, plus the newest NeoForge release. 1.21.2,
        // 1.21.3 and 1.21.9 are deliberately absent: GeckoLib and/or Curios, both required
        // dependencies, have no build for them. 1.21.5 is started but not finished; its
        // conditionals are already in the sources and adding it back here is the only step needed.
        match("1.20.1", "forge", "fabric")
        match("1.21.1", "neoforge", "fabric")
        match("1.21.10", "neoforge")
        vcsVersion = "1.21.1-neoforge"
    }
}

rootProject.name = "EyPipesMod"
