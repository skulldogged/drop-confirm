import io.github.klahap.dotenv.DotEnvBuilder
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_16
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.mod.publish)
  alias(libs.plugins.modstitch.base)
  alias(libs.plugins.dotenv)
}

fun getDep(name: String): String =
  findProperty("deps.$name") as? String? ?: throw IllegalStateException("$name is not defined for $minecraft-$loader")

fun getDepOrNull(name: String): String? =
  findProperty("deps.$name") as? String?

val minecraft = getDep("minecraft")

/** Fabric Language Kotlin release used for dev runs when a version does not pin one. */
val defaultFabricKotlin = "1.13.10+kotlin.2.3.20"

val java = when {
  sc.current.parsed >= "26.1" -> 25 to JVM_25
  sc.current.parsed >= "1.20.5" -> 21 to JVM_21
  sc.current.parsed >= "1.18" -> 17 to JVM_17
  sc.current.parsed >= "1.17" -> 16 to JVM_16
  else -> 8 to JVM_1_8
}

val loader = when {
  modstitch.isLoom -> "fabric"
  modstitch.isModDevGradleRegular -> "neoforge"
  modstitch.isModDevGradleLegacy -> "forge"
  else -> throw IllegalStateException("Unsupported loader")
}

modstitch {
  minecraftVersion = minecraft
  javaVersion = java.first
  parchment.mappingsVersion = getDepOrNull("parchment")

  metadata {
    modId = "drop_confirm"
    modName = "DropConfirm"
    modVersion = "6.2.0"
    modGroup = "dev.skulldogged.drop_confirm"
    modAuthor = "skulldogged"
    modDescription = "Think twice before you drop. Adds a confirmation prompt when dropping items."
    modLicense = "MIT"

    replacementProperties.put("java", "${java.first}")
    replacementProperties.put("minecraft_range", getDepOrNull("minecraftRange")
      ?: if (loader == "fabric") ">=$minecraft" else "[$minecraft,)")

    if (loader == "fabric")
      replacementProperties.put("fabric_api", getDep("fabric-api"))

    replacementProperties.put(
      "fabric_key_api",
      if (sc.current.parsed >= "26.1") "fabric-key-mapping-api-v1" else "fabric-key-binding-api-v1"
    )

    replacementProperties.put(
      "loader_version", when (loader) {
        "fabric" -> getDepOrNull("fabric-loader") ?: "0.18.4"
        else -> getDep(loader)
      }
    )

    replacementProperties.put("fabric_resource_api",
      if (sc.current.parsed >= "26.3") "fabric-resource-loader-v1" else "fabric-resource-loader-v0"
    )
    replacementProperties.put("fabric_kotlin_range",
      getDepOrNull("fabric-language-kotlin")?.let { ">=$it" } ?: "*"
    )

    replacementProperties.put("mod_sources", "https://github.com/skulldogged/drop-confirm")
    replacementProperties.put("mod_issue_tracker", "https://github.com/skulldogged/drop-confirm/issues")
    replacementProperties.put("pack_format", getDep("pack_format"))
    replacementProperties.put("neoforge_icon_property", if (sc.current.parsed >= "26.3") "iconFile" else "logoFile")
  }

  loom { fabricLoaderVersion = getDepOrNull("fabric-loader") ?: "0.18.4" }

  moddevgradle {
    defaultRuns()

    listOf(
      "forge" to ::forgeVersion,
      "neoforge" to ::neoForgeVersion
    ).forEach { (name, setter) -> (findProperty("deps.$name") as? String?)?.let { setter().set(it) } }
  }

  mixin {
    addMixinsToModManifest = true
    configs.register("drop_confirm")
  }

  kotlin {
    jvmToolchain(java.first)
    compilerOptions.jvmTarget.set(java.second)
  }
}

if (loader == "fabric") {
  extensions.configure<net.fabricmc.loom.api.LoomGradleExtensionAPI>("loom") {
    enableTransitiveAccessWideners.set(false)
  }
}

tasks {
  named<ProcessResources>("generateModMetadata") {
    duplicatesStrategy = INCLUDE
    dependsOn("stonecutterGenerate")
  }

  named("compileKotlin") { dependsOn("stonecutterGenerate") }
  processResources {
    duplicatesStrategy = INCLUDE

    if (loader != "forge")
      exclude("META-INF/services/org.spongepowered.asm.mixin.connect.IMixinConnector")
  }
}

sc {
  constants {
    val loader: String = current.project.substringAfter('-')
    match(loader, "fabric", "forge", "neoforge")
  }

  swaps["bus_subscriber_import"] = when {
    current.parsed <= "1.20.4" -> "Mod.EventBusSubscriber"
    else -> "EventBusSubscriber"
  }
  swaps["config_screen_factory_import"] = when {
    current.parsed <= "1.20.4" -> "ConfigScreenHandler.ConfigScreenFactory"
    else -> "gui.IConfigScreenFactory as ConfigScreenFactory"
  }
  swaps["fml_env_dist"] = when {
    current.parsed >= "1.21.9" -> "getDist()"
    else -> "dist"
  }
  swaps["item_style"] = when {
    (current.parsed >= "1.20.6" && loader == "neoforge") || loader == "forge" -> "itemStack.rarity.styleModifier"
    current.parsed >= "1.20.6" && loader == "fabric" -> "itemStack.rarity.color()"
    else -> "itemStack.rarity.color"
  }
  swaps["render_method"] = when {
    current.parsed >= "26.1" -> "extractContents"
    current.parsed >= "1.21.11" -> "renderContents"
    current.parsed >= "1.20.4" -> "renderWidget"
    current.parsed >= "1.17.1" -> "render"
    else -> "renderButton"
  }
  swaps["slot_change_method"] = when {
    current.parsed >= "1.21.5" -> "\"setSelectedSlot\""
    current.parsed eq "1.21.4" -> "\"setSelectedHotbarSlot\""
    else -> "\"tick\""
  }
  swaps["get_selected_item"] = when {
    current.parsed >= "1.21.5" -> "getSelectedItem"
    else -> "getSelected"
  }
  swaps["drop_sound"] = when {
    current.parsed >= "1.18.2" -> "BUNDLE_DROP_CONTENTS"
    else -> "ITEM_PICKUP"
  }
  swaps["identifier_type"] = when {
    current.parsed >= "1.21.11" -> "Identifier"
    else -> "ResourceLocation"
  }
  swaps["gui_graphics_type"] = when {
    current.parsed >= "26.1" -> "GuiGraphicsExtractor"
    else -> "GuiGraphics"
  }
  swaps["draw_string_fn"] = when {
    current.parsed >= "26.1" -> "text"
    else -> "drawString"
  }
  swaps["draw_centered_string_fn"] = when {
    current.parsed >= "26.1" -> "centeredText"
    else -> "drawCenteredString"
  }
  swaps["screen_render_fn"] = when {
    current.parsed >= "26.1" -> "extractRenderState"
    else -> "render"
  }
  swaps["add_widget_fn"] = when {
    current.parsed >= "1.17.1" -> "addRenderableWidget"
    else -> "addButton"
  }
}

dependencies {
  modstitch {
    loom {
      listOf(
        "fabric-api-base",
        "fabric-lifecycle-events-v1",
        if (sc.current.parsed >= "26.1") "fabric-key-mapping-api-v1" else "fabric-key-binding-api-v1",
        if (sc.current.parsed >= "26.3") "fabric-resource-loader-v1" else "fabric-resource-loader-v0"
      ).forEach {
        modstitchModImplementation(
          (project.extensions.findByName("fabricApi") as FabricApiExtension)
            .module(it, getDep("fabric-api"))
        )
      }

      getDepOrNull("mixinExtras")?.let { modstitchModImplementation(it) }
      getDepOrNull("fabric-language-kotlin")?.let {
        modstitchModImplementation("net.fabricmc:fabric-language-kotlin:$it")
      }

      modstitchModImplementation("maven.modrinth:modmenu:${getDep("modmenu")}")
    }

    moddevgradle {
      getDepOrNull("kotlinForForge")?.let {
        if (loader == "neoforge") modstitchImplementation(it)
        else modstitchModImplementation(it)
      }

      if (loader != "neoforge") {
        modstitchImplementation(libs.mixin)
        annotationProcessor(libs.mixin)
      }
    }
  }
}

// Fabric Language Kotlin is a runtime dependency of the mod. Versions that do not pin a release
// still need it on the dev runtime classpath, otherwise Fabric Loader refuses to start the client.
// Mod Menu (dev only) additionally needs the Fabric screen API module on newer versions.
if (loader == "fabric") {
  configurations.matching { it.name == "modLocalRuntime" }.configureEach {
    if (getDepOrNull("fabric-language-kotlin") == null)
      dependencies.add(project.dependencies.create("net.fabricmc:fabric-language-kotlin:$defaultFabricKotlin"))

    if (sc.current.parsed >= "1.16.5")
      dependencies.add(
        project.dependencies.create(
          (project.extensions.findByName("fabricApi") as FabricApiExtension)
            .module("fabric-screen-api-v1", getDep("fabric-api"))
        )
      )
  }
}

publishMods {
  val envFilePath = rootDir.resolve(".env")

  val envVars = DotEnvBuilder.dotEnv {
    addSystemEnv()

    if (envFilePath.exists() && envFilePath.isFile)
      addFile(envFilePath.absolutePath)
  }

  val supportedVersions = getDepOrNull("mcRange") ?: getDep("minecraft")

  val supportedVersionsList = supportedVersions.split(',')
    .map { it.trim() }
    .filter { it.isNotEmpty() }

  val versionRangeString: String = when {
    supportedVersionsList.isEmpty() -> ""
    supportedVersionsList.size == 1 -> supportedVersionsList.first()
    else -> "${supportedVersionsList.first()}-${supportedVersionsList.last()}"
  }

  val displayVersionSuffix = if (versionRangeString.isNotEmpty()) " ($versionRangeString)" else ""
  val releaseDisplayName =
    "${modstitch.metadata.modName.get()} ${modstitch.metadata.modVersion.get()}$displayVersionSuffix"

  type = STABLE
  file.set((if (loader == "forge" && sc.current.parsed >= "1.18") tasks.named("jar") else modstitch.finalJarTask).flatMap { (it as AbstractArchiveTask).archiveFile })
  displayName = releaseDisplayName

  changelog = """
    DropConfirm no longer depends on a config library. The settings screen is now built into the mod,
    so YetAnotherConfigLib and UniLib are no longer required on any version.

    ## Dependencies

    ### Required
      * ${getDep("changelogKotlin")}

      ${
    if (loader == "fabric") """
    ### Recommended
      * ModMenu `v${getDep("modmenu")}` or newer.
    """ else ""
  }
  """.trimIndent()

  modLoaders.add(loader)

  github("github") {
    accessToken.set(envVars["GITHUB_TOKEN"])
    repository.set("skulldogged/drop-confirm")
    commitish.set("master")
    tagName.set("v${modstitch.metadata.modVersion.get()}-$minecraft-$loader")
  }

  curseforge("curseforge") {
    accessToken.set(envVars["CURSEFORGE_TOKEN"])
    projectId.set("881314")
    minecraftVersions.addAll(supportedVersionsList)

    if (loader == "fabric") {
      requires("fabric-api")
      requires("fabric-language-kotlin")
      optional("modmenu")
    } else {
      requires("kotlin-for-forge")
    }
  }

  modrinth("modrinth") {
    accessToken.set(envVars["MODRINTH_TOKEN"])
    projectId.set("I45rjF2F")
    minecraftVersions.addAll(supportedVersionsList)

    if (loader == "fabric") {
      requires("fabric-api")
      requires("fabric-language-kotlin")
      optional("modmenu")
    } else {
      requires("kotlin-for-forge")
    }
  }
}
