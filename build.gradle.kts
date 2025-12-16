import io.github.klahap.dotenv.DotEnvBuilder
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.file.DuplicatesStrategy.INCLUDE
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

val java = when {
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
    modVersion = "6.1.0"
    modGroup = "xyz.pupbrained.drop_confirm"
    modAuthor = "pupbrained"
    modDescription = "Think twice before you drop. Adds a confirmation prompt when dropping items."
    modLicense = "MIT"

    replacementProperties.put("java", "${java.first}")

    if (loader == "fabric")
      replacementProperties.put("fabric_api", getDep("fabric-api"))

    replacementProperties.put(
      "loader_version", when (loader) {
        "fabric" -> "0.18.2"
        else -> getDep(loader)
      }
    )

    replacementProperties.put("config_lib", getDep("configLibName"))

    replacementProperties.put("config_lib_version", getDep("configLibVersion"))

    replacementProperties.put("mod_sources", "https://github.com/pupbrained/drop-confirm")
    replacementProperties.put("mod_issue_tracker", "https://github.com/pupbrained/drop-confirm/issues")
    replacementProperties.put("pack_format", getDep("pack_format"))
  }

  loom { fabricLoaderVersion = "0.18.2" }

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
}

dependencies {
  modstitchModImplementation(getDep("configLib"))

  if (sc.current.parsed < "1.20.1" || loader == "forge") {
    modstitchImplementation(libs.json)
    modstitchJiJ(libs.json)
  }

  modstitch {
    loom {
      listOf(
        "fabric-api-base",
        "fabric-lifecycle-events-v1",
        "fabric-key-binding-api-v1",
        "fabric-resource-loader-v0"
      ).forEach {
        modstitchModImplementation(
          (project.extensions.findByName("fabricApi") as FabricApiExtension)
            .module(it, getDep("fabric-api"))
        )
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
    This update adds support for Minecraft 1.21.11 and fixes some issues:
      * Fixed an issue where DropConfirm used packet modifications to cancel drops, which could trip some anticheats.
      * Fixed a crash on 1.21.1 + NeoForge related to an incorrect annotation.
      * Fixed an issue with metadata for 1.21.10 on fabric.

    ## Dependencies

    ### Required
      * ${getDep("changelogConfigLib")}
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
    repository.set("pupbrained/drop-confirm")
    commitish.set("master")
    tagName.set("v${modstitch.metadata.modVersion.get()}-$minecraft-$loader")
  }

  curseforge("curseforge") {
    accessToken.set(envVars["CURSEFORGE_TOKEN"])
    projectId.set("881314")
    minecraftVersions.addAll(supportedVersionsList)

    requires(
      when {
        sc.current.parsed >= "1.20.1" && loader != "forge" -> "yacl"
        else -> "unilib"
      }
    )

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

    requires(
      when {
        sc.current.parsed >= "1.20.1" && loader != "forge" -> "yacl"
        else -> "unilib"
      }
    )
    if (loader == "fabric") {
      requires("fabric-api")
      requires("fabric-language-kotlin")
      optional("modmenu")
    } else {
      requires("kotlin-for-forge")
    }
  }
}
