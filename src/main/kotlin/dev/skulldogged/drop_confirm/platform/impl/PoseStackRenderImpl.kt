//? if >=1.16.5 && <1.20.1 {
/*package dev.skulldogged.drop_confirm.platform.impl

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import dev.skulldogged.drop_confirm.platform.RenderInterface
import dev.skulldogged.drop_confirm.util.ComponentUtils

class PoseStackRenderImpl(private val poseStack: PoseStack) : RenderInterface {
  object ScreenBridge : Screen(ComponentUtils.empty()) {
    fun exposedFillGradient(
      poseStack: PoseStack,
      x1: Int, y1: Int,
      x2: Int, y2: Int,
      colorFrom: Int, colorTo: Int
    ) = fillGradient(poseStack, x1, y1, x2, y2, colorFrom, colorTo)
  }

  override fun drawString(font: Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean): RenderInterface {
    if (shadow)
      font.drawShadow(poseStack, text, x.toFloat(), y.toFloat(), color)
    else
      font.draw(poseStack, text, x.toFloat(), y.toFloat(), color)

    return this
  }

  override fun drawCenteredString(font: Font, text: String, x: Int, y: Int, color: Int): RenderInterface {
    Screen.drawCenteredString(poseStack, font, text, x, y, color)

    return this
  }

  //? if >1.16.5 {
  override fun drawCenteredString(
    font: Font,
    text: FormattedCharSequence,
    x: Int,
    y: Int,
    color: Int
  ): RenderInterface {
    Screen.drawCenteredString(poseStack, font, text, x, y, color)

    return this
  }
  //?}

  override fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Int): RenderInterface {
    Screen.fill(poseStack, x1, y1, x2, y2, color)

    return this
  }

  override fun fillGradient(x1: Int, y1: Int, x2: Int, y2: Int, colorStart: Int, colorEnd: Int): RenderInterface {
    ScreenBridge.exposedFillGradient(poseStack, x1, y1, x2, y2, colorStart, colorEnd)

    return this
  }
}
*///?}
