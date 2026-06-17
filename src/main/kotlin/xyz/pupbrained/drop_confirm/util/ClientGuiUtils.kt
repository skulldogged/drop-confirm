package xyz.pupbrained.drop_confirm.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object ClientGuiUtils {
  @JvmStatic
  fun setScreen(minecraft: Minecraft?, screen: Screen?) {
    minecraft ?: return

    //? if >=26.2 {
    /*minecraft.gui.setScreen(screen)
    *///?} else {
    minecraft.setScreen(screen)
    //?}
  }

  @JvmStatic
  fun setOverlayMessage(minecraft: Minecraft, message: Component, animateColor: Boolean) {
    //? if >=26.2 {
    /*minecraft.gui.hud.setOverlayMessage(message, animateColor)
    *///?} else {
    minecraft.gui.setOverlayMessage(message, animateColor)
    //?}
  }
}
