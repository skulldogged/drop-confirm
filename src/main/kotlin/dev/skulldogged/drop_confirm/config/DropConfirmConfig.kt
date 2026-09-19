package dev.skulldogged.drop_confirm.config

//? if fabric {
import net.fabricmc.loader.api.FabricLoader
//?} else {
/*import net.neoforged.fml.loading.FMLPaths
*///?}

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import net.minecraft.world.item.Item
import dev.skulldogged.drop_confirm.DropConfirm
import dev.skulldogged.drop_confirm.util.ItemUtils

enum class ConfirmationMode {
  POPUP, ACTIONBAR, CHAT;

  val translationKey: String
    get() = "option.drop_confirm.confirmation_mode.${name.lowercase()}"
}

/**
 * The mod's settings, persisted as a JSON5 file in the loader's config directory.
 *
 * Reading uses Gson's lenient parser so comments written by older versions of the
 * mod (or by users) are accepted. Writing is done by hand so each option keeps a
 * descriptive comment in the file.
 */
object DropConfirmConfig {
  @JvmStatic
  @get:JvmName("isEnabled")
  var enabled = true

  @JvmStatic
  @get:JvmName("shouldPlaySounds")
  var shouldPlaySounds = true

  @JvmStatic
  @get:JvmName("shouldTreatAsWhitelist")
  var treatAsWhitelist = false

  @JvmStatic
  @get:JvmName("getResetDelay")
  var confirmationResetDelay = 1.0F

  @JvmStatic
  @get:JvmName("getConfirmationMode")
  var confirmationMode = ConfirmationMode.ACTIONBAR

  @JvmStatic
  @get:JvmName("getBlacklistedItems")
  var blacklistedItems: MutableList<Item> = mutableListOf()

  private val gson = Gson()

  val configFile: Path =
    (/*? if fabric {*/FabricLoader.getInstance().configDir/*?} else {*//*FMLPaths.CONFIGDIR.get()*//*?}*/)
      .resolve("drop_confirm.json5")

  private var isLoaded = false

  fun load() {
    if (isLoaded) return

    try {
      if (configFile.exists()) read()
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to load DropConfirm config from ${configFile.absolutePathString()}", e)
    } finally {
      if (!configFile.exists()) save()
      isLoaded = true
    }
  }

  private fun read() {
    Files.newBufferedReader(configFile, StandardCharsets.UTF_8).use { source ->
      val reader = JsonReader(source)
      reader.isLenient = true

      if (reader.peek() != JsonToken.BEGIN_OBJECT) return
      reader.beginObject()

      while (reader.hasNext()) {
        when (reader.nextName()) {
          "enabled" -> enabled = reader.nextBoolean()
          "playSounds", "shouldPlaySounds" -> shouldPlaySounds = reader.nextBoolean()
          "treatAsWhitelist" -> treatAsWhitelist = reader.nextBoolean()
          "confirmationResetDelay" -> confirmationResetDelay = reader.nextDouble().toFloat()

          "confirmationMode" -> {
            val name = reader.nextString()
            ConfirmationMode.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
              ?.let { confirmationMode = it }
          }

          "blacklistedItems" -> {
            val items = mutableListOf<Item>()
            reader.beginArray()
            while (reader.hasNext()) {
              if (reader.peek() == JsonToken.STRING) {
                ItemUtils.lookup(reader.nextString())?.let { if (it !in items) items.add(it) }
              } else {
                reader.skipValue()
              }
            }
            reader.endArray()
            blacklistedItems = items
          }

          else -> reader.skipValue()
        }
      }

      reader.endObject()
    }
  }

  fun save() {
    try {
      configFile.parent.createDirectories()

      val ids = blacklistedItems.map { ItemUtils.idOf(it) }
      val text = buildString {
        appendLine("{")
        appendLine("  // Whether DropConfirm is enabled")
        appendLine("  \"enabled\": $enabled,")
        appendLine()
        appendLine("  // Whether to play sounds when confirming/canceling drops")
        appendLine("  \"playSounds\": $shouldPlaySounds,")
        appendLine()
        appendLine("  // If true, the item list below is treated as a whitelist instead of a blacklist")
        appendLine("  \"treatAsWhitelist\": $treatAsWhitelist,")
        appendLine()
        appendLine("  // How long (in seconds) until the confirmation is reset")
        appendLine("  \"confirmationResetDelay\": $confirmationResetDelay,")
        appendLine()
        appendLine("  // Where the confirmation is shown (${ConfirmationMode.entries.joinToString(", ") { it.name }})")
        appendLine("  \"confirmationMode\": ${gson.toJson(confirmationMode.name)},")
        appendLine()
        appendLine("  // Items to blacklist (or whitelist, if treatAsWhitelist is true)")
        if (ids.isEmpty()) {
          appendLine("  \"blacklistedItems\": []")
        } else {
          appendLine("  \"blacklistedItems\": [")
          ids.forEachIndexed { index, id ->
            appendLine("    ${gson.toJson(id)}${if (index < ids.lastIndex) "," else ""}")
          }
          appendLine("  ]")
        }
        appendLine("}")
      }

      Files.write(configFile, text.toByteArray(StandardCharsets.UTF_8))
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to save DropConfirm config to ${configFile.absolutePathString()}", e)
    }
  }
}
