pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.kikugie.dev/snapshots")
  }
}

plugins { id("dev.kikugie.stonecutter") version "0.8-beta.2" }

sc {
  kotlinController = true
  centralScript = "build.gradle.kts"

  create(rootProject) {
    fun mc(mcVersion: String, vararg loaders: String) = loaders.forEach { version("$mcVersion-$it", mcVersion) }

    mc("1.14.4", "fabric")
    mc("1.15.2", "fabric")
    mc("1.16.5", "fabric")
    mc("1.17.1", "fabric")

    mc("1.18.2", "fabric", "forge")
    mc("1.19.4", "fabric", "forge")
    mc("1.20.1", "fabric", "forge")

    mc("1.20.4", "fabric")

    mc("1.20.6", "fabric", "neoforge")
    mc("1.21.1", "fabric", "neoforge")
    mc("1.21.3", "fabric", "neoforge")
    mc("1.21.4", "fabric", "neoforge")
    mc("1.21.5", "fabric", "neoforge")
    mc("1.21.6", "fabric", "neoforge")
    mc("1.21.9", "fabric", "neoforge")
    mc("1.21.11", "fabric", "neoforge")

    vcsVersion = "1.21.4-fabric"
  }
}

rootProject.name = "DropConfirm"
