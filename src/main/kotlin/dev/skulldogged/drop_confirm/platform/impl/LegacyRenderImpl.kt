//? if <=1.15.2 {
/*package dev.skulldogged.drop_confirm.platform.impl

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.Screen
import dev.skulldogged.drop_confirm.platform.RenderInterface
import dev.skulldogged.drop_confirm.util.ComponentUtils

class LegacyRenderImpl : RenderInterface {
  object ScreenBridge : Screen(ComponentUtils.empty()) {
    fun exposedFillGradient(
      x1: Int, y1: Int,
      x2: Int, y2: Int,
      colorFrom: Int, colorTo: Int
    ) = fillGradient(x1, y1, x2, y2, colorFrom, colorTo)

    fun exposedDrawCenteredString(
      font: Font,
      text: String,
      x: Int,
      y: Int,
      color: Int
    ) = drawCenteredString(font, text, x, y, color)
  }

  override fun drawString(font: Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean): RenderInterface {
    if (shadow)
      font.drawShadow(text, x.toFloat(), y.toFloat(), color)
    else
      font.draw(text, x.toFloat(), y.toFloat(), color)

    return this
  }

  override fun drawCenteredString(font: Font, text: String, x: Int, y: Int, color: Int): RenderInterface {
    ScreenBridge.exposedDrawCenteredString(font, text, x, y, color)

    return this
  }

  override fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Int): RenderInterface {
    Screen.fill(x1, y1, x2, y2, color)

    return this
  }

  override fun fillGradient(x1: Int, y1: Int, x2: Int, y2: Int, colorStart: Int, colorEnd: Int): RenderInterface {
    ScreenBridge.exposedFillGradient(x1, y1, x2, y2, colorStart, colorEnd)

    return this
  }
}
*///?}
