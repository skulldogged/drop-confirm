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
import net.minecraft.resources./*$ identifier_type {*/ResourceLocation/*$}*/
import net.minecraft.sounds.SoundEvents
//? if <26.3
import org.lwjgl.glfw.GLFW
import dev.skulldogged.drop_confirm.config.DropConfirmConfig
import dev.skulldogged.drop_confirm.util.ClientGuiUtils
import dev.skulldogged.drop_confirm.util.ComponentUtils

object DropConfirm {
  @JvmStatic
  val LOGGER: Logger =
    /*? if >=1.18.2 {*/LoggerFactory.getLogger("DropConfirm")/*?} else {*//*LogManager.getLogger("DropConfirm")*//*?}*/

  @JvmStatic var isConfirmed = false

  val TOGGLE_KEY = KeyMapping(
    "key.drop_confirm.toggle",
    InputConstants.Type./*? if >=26.3 {*//*KEYBOARD*//*?} else {*/KEYSYM/*?}*/,
    /*? if >=26.3 {*//*InputConstants.KEY_J*//*?} else {*/GLFW.GLFW_KEY_J/*?}*/,
    //? if >=1.21.9 {
    /*KeyMapping.Category(
      /*$ identifier_type {*/ResourceLocation/*$}*/
        .fromNamespaceAndPath("drop_confirm", "main")
    )
    *///?} else {
    "key.category.drop_confirm.main"
    //?}
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

          ClientGuiUtils.setOverlayMessage(
            mc,
            ComponentUtils.literal("DropConfirm: ").append(
              ComponentUtils
                .translatable(if (enabled) "drop_confirm.toggle.on" else "drop_confirm.toggle.off")
                .withStyle(if (enabled) ChatFormatting.GREEN else ChatFormatting.RED)
            ),
            false
          )
        }
      }
    }
  }
}
