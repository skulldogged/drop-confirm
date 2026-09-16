package dev.skulldogged.drop_confirm.config

//? if >=1.20.1 && !forge {
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import net.minecraft.world.item.Item
import dev.skulldogged.drop_confirm.util.ComponentUtils

enum class ConfirmationMode : NameableEnum {
  POPUP, ACTIONBAR, CHAT;

  override fun getDisplayName() =
    ComponentUtils.translatable("option.drop_confirm.confirmation_mode.${name.lowercase()}")
}

object DropConfirmConfig {
  class DropConfirmConfigInternal {
    @SerialEntry var enabled = true
    @SerialEntry var shouldPlaySounds = true
    @SerialEntry var treatAsWhitelist = false
    @SerialEntry var confirmationResetDelay = 1.0
    @SerialEntry var confirmationMode = ConfirmationMode.ACTIONBAR
    @SerialEntry var blacklistedItems: List<Item> = emptyList()
  }

  @JvmStatic
  @get:JvmName("isEnabled")
  var enabled: Boolean by ConfigDelegate(DropConfirmConfigInternal::enabled)

  @JvmStatic
  @get:JvmName("shouldPlaySounds")
  var shouldPlaySounds: Boolean by ConfigDelegate(DropConfirmConfigInternal::shouldPlaySounds)

  @JvmStatic
  @get:JvmName("shouldTreatAsWhitelist")
  var treatAsWhitelist: Boolean by ConfigDelegate(DropConfirmConfigInternal::treatAsWhitelist)

  @JvmStatic
  @get:JvmName("getResetDelay")
  var confirmationResetDelay: Double by ConfigDelegate(DropConfirmConfigInternal::confirmationResetDelay)

  @JvmStatic
  @get:JvmName("getConfirmationMode")
  var confirmationMode: ConfirmationMode by ConfigDelegate(DropConfirmConfigInternal::confirmationMode)

  @JvmStatic
  @get:JvmName("getBlacklistedItems")
  var blacklistedItems: List<Item> by ConfigDelegate(DropConfirmConfigInternal::blacklistedItems)

  val HANDLER: ConfigClassHandler<DropConfirmConfigInternal> =
    ConfigClassHandler.createBuilder(DropConfirmConfigInternal::class.java)
      .serializer {
        GsonConfigSerializerBuilder.create(it)
          .setPath(YACLPlatform.getConfigDir().resolve("drop_confirm.json5"))
          .setJson5(true)
          .build()
      }
      .build()

  fun save() = HANDLER.save()
  fun load() = HANDLER.load()

  private class ConfigDelegate<T>(private val propertyRef: KMutableProperty1<DropConfirmConfigInternal, T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = propertyRef.get(HANDLER.instance())
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = propertyRef.set(HANDLER.instance(), value)
  }
}
//?} else {
/*//? if fabric {
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core./*? if <=1.18.2 {*//*Registry as BuiltInRegistries*//*?} else {*/registries.BuiltInRegistries/*?}*/
//?} else {
/*import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.registries.ForgeRegistries as BuiltInRegistries
*///?}

import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import dev.skulldogged.drop_confirm.DropConfirm

import org.quiltmc.parsers.json.JsonReader
import org.quiltmc.parsers.json.JsonWriter

enum class ConfirmationMode {
  POPUP, ACTIONBAR, CHAT;

  fun getTranslationKey(): String = "option.drop_confirm.confirmation_mode.${name.lowercase()}"
}

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

  private val configFile =
    /*? if fabric {*/FabricLoader.getInstance().configDir/*?} else {*//*FMLPaths.CONFIGDIR.get()*//*?}*/
    .resolve("drop_confirm.json5")

  private var isLoaded = false

  fun load() {
    if (isLoaded) return

    try {
      if (configFile.exists()) {
        JsonReader.json5(configFile).use { reader ->
          reader.beginObject()

          while (reader.hasNext()) {
            val name = reader.nextName()
            when (name) {
              "enabled" -> enabled = reader.nextBoolean()
              "playSounds" -> shouldPlaySounds = reader.nextBoolean()
              "treatAsWhitelist" -> treatAsWhitelist = reader.nextBoolean()
              "confirmationResetDelay" -> confirmationResetDelay = reader.nextDouble().toFloat()

              "confirmationMode" -> {
                val modeName = reader.nextString()
                runCatching { ConfirmationMode.valueOf(modeName.uppercase()) }
                  .getOrNull()?.let { mode -> confirmationMode = mode }
              }

              "blacklistedItems" -> {
                val items = mutableListOf<Item>()
                reader.beginArray()
                while (reader.hasNext()) {
                  val itemId = reader.nextString()
                  val fullId = if (itemId.contains(":")) itemId else "minecraft:$itemId"
                  val item = BuiltInRegistries./*? if fabric {*/ITEM.get/*?} else {*//*ITEMS.getValue*//*?}*/(
                    ResourceLocation.tryParse(fullId)
                  )
                  //? if >=1.18.2 {
                  @Suppress("SENSELESS_COMPARISON")
                  if (item != null)
                  //?}
                  items.add(item)
                }
                reader.endArray()
                blacklistedItems = items
              }

              else -> reader.skipValue() // Skip unknown properties
            }
          }

          reader.endObject()
        }
      }
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to load configuration", e)
    } finally {
      if (!configFile.exists()) save()
      isLoaded = true
    }
  }

  fun save() {
    try {
      configFile.parent.createDirectories()

      // Convert Item objects to their string identifiers before saving
      val itemIdentifiers = blacklistedItems.map { item ->
        BuiltInRegistries./*? if fabric {*/ITEM/*?} else {*//*ITEMS*//*?}*/.getKey(item).toString()
      }

      // Use Quilt-parsers JsonWriter instead of Gson
      JsonWriter.json5(configFile).use { writer ->
        writer.setIndent("  ")

        writer.beginObject()

        writer.comment("Whether DropConfirm is enabled")
        writer.name("enabled").value(enabled)

        writer.comment("Whether to play sounds when confirming/canceling drops")
        writer.name("playSounds").value(shouldPlaySounds)

        writer.comment("If true, blacklisted items will be treated as a whitelist instead")
        writer.name("treatAsWhitelist").value(treatAsWhitelist)

        writer.comment("How long (in seconds) until the confirmation is reset")
        writer.name("confirmationResetDelay").value(confirmationResetDelay)

        writer.comment("The confirmation mode (DIALOG, HOTBAR, CHAT)")
        writer.name("confirmationMode").value(confirmationMode.name)

        writer.comment("The list of items to blacklist (or whitelist if treatAsWhitelist is true)")
        writer.name("blacklistedItems")

        writer.beginArray()
        itemIdentifiers.forEach { writer.value(it) }
        writer.endArray()

        writer.endObject()
      }
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to save DropConfirm config to ${configFile.absolutePathString()}", e)
    }
  }
}
*///?}
