package xyz.pupbrained.drop_confirm

//? if <1.20.1 || forge {
/*import io.github.cdagaming.unicore.utils.TranslationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
*///?}

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.resources./*$ identifier_type {*/ResourceLocation/*$}*/
import net.minecraft.sounds.SoundEvents
import org.lwjgl.glfw.GLFW
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.util.ClientGuiUtils
import xyz.pupbrained.drop_confirm.util.ComponentUtils

object DropConfirm {
  //? if <1.20.1 || forge {
  /*@JvmStatic val TRANSLATOR: TranslationUtils =
    TranslationUtils("drop_confirm", true)
      .setDefaultLanguage("en_us")
      .build()

  @JvmStatic val LOGGER: Logger = LogManager.getLogger("DropConfirm")
  *///?}

  @JvmStatic var isConfirmed = false

  val TOGGLE_KEY = KeyMapping(
    "key.drop_confirm.toggle",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_J,
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
