package dev.skulldogged.drop_confirm

//? if >=1.18.2 {
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//?} else {
/*import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
*///?}

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources./*$ identifier_type {*/ResourceLocation/*$}*/
import net.minecraft.sounds.SoundEvents
//? if <26.3
import org.lwjgl.glfw.GLFW
import dev.skulldogged.drop_confirm.config.DropConfirmConfig
import dev.skulldogged.drop_confirm.config.ExactItem
import dev.skulldogged.drop_confirm.util.ClientGuiUtils
import dev.skulldogged.drop_confirm.util.ComponentUtils

object DropConfirm {
  @JvmStatic
  val LOGGER: Logger =
    /*? if >=1.18.2 {*/LoggerFactory.getLogger("DropConfirm")/*?} else {*//*LogManager.getLogger("DropConfirm")*//*?}*/

  @JvmStatic var isConfirmed = false

  private val KEY_CATEGORY =
    //? if >=1.21.9 {
    /*KeyMapping.Category(
      /*$ identifier_type {*/ResourceLocation/*$}*/
        .fromNamespaceAndPath("drop_confirm", "main")
    )
    *///?} else {
    "key.category.drop_confirm.main"
    //?}

  private val KEY_TYPE = InputConstants.Type./*? if >=26.3 {*//*KEYBOARD*//*?} else {*/KEYSYM/*?}*/

  /** Toggles the mod on and off. */
  val TOGGLE_KEY = KeyMapping(
    "key.drop_confirm.toggle",
    KEY_TYPE,
    /*? if >=26.3 {*//*InputConstants.KEY_J*//*?} else {*/GLFW.GLFW_KEY_J/*?}*/,
    KEY_CATEGORY
  )

  /** Adds or removes the held item, as a specific stack, from the item list. Unbound by default. */
  val TOGGLE_HELD_KEY = KeyMapping(
    "key.drop_confirm.toggle_held",
    KEY_TYPE,
    InputConstants.UNKNOWN.value,
    KEY_CATEGORY
  )

  fun handleKeyPresses(mc: Minecraft) {
    while (TOGGLE_KEY.consumeClick()) {
      DropConfirmConfig.apply {
        //? if 1.14.4
        /*@Suppress("UNNECESSARY_SAFE_CALL")*/
        mc.player?.let {
          enabled = !enabled
          isConfirmed = false
          save()

          if (shouldPlaySounds)
            it.playSound(SoundEvents.ITEM_PICKUP, 1.0f, if (enabled) 1.0f else 0.5f)

          showStatus(
            mc,
            ComponentUtils
              .translatable(if (enabled) "drop_confirm.toggle.on" else "drop_confirm.toggle.off")
              .withStyle(if (enabled) ChatFormatting.GREEN else ChatFormatting.RED)
          )
        }
      }
    }

    while (TOGGLE_HELD_KEY.consumeClick()) toggleHeldItem(mc)
  }

  private fun toggleHeldItem(mc: Minecraft) {
    //? if 1.14.4
    /*@Suppress("UNNECESSARY_SAFE_CALL")*/
    val player = mc.player ?: return
    val stack = player.mainHandItem

    if (stack.isEmpty) {
      showStatus(mc, ComponentUtils.translatable("drop_confirm.exact.empty_hand").withStyle(ChatFormatting.RED))
      return
    }

    val entry = ExactItem.capture(stack) ?: return
    val config = DropConfirmConfig
    val existing = config.exactItems.firstOrNull { it == entry || (entry.tag != null && it.tag == entry.tag) }

    val message = if (existing != null) {
      config.exactItems.remove(existing)
      ComponentUtils.translatable("drop_confirm.exact.removed", existing.name).withStyle(ChatFormatting.YELLOW)
    } else {
      config.exactItems.add(entry)
      ComponentUtils.translatable("drop_confirm.exact.added", entry.name).withStyle(ChatFormatting.GREEN)
    }

    config.save()
    showStatus(mc, message)
  }

  private fun showStatus(mc: Minecraft, message: Component) =
    ClientGuiUtils.setOverlayMessage(mc, ComponentUtils.literal("DropConfirm: ").append(message), false)
}
