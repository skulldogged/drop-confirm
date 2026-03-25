plugins {
  id("dev.kikugie.stonecutter")
  alias(libs.plugins.mod.publish)

  id("fabric-loom") version "1.15-SNAPSHOT" apply false
}

stonecutter active "1.21.4-fabric"

tasks.register("Build active project") {
  group = "stonecutter"
  dependsOn(":${stonecutter.current?.project}:build")
}

tasks.register("Run active project") {
  group = "stonecutter"
  dependsOn(":${stonecutter.current?.project}:runClient")
}

allprojects {
  repositories {
    mavenCentral()
    mavenLocal()
    maven("https://api.modrinth.com/maven")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.firstdark.dev/snapshots")
    maven("https://maven.isxander.dev/releases/")
    maven("https://maven.neoforged.net/releases")
    maven("https://maven.nucleoid.xyz/")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
  }
}
