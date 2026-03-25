package xyz.pupbrained.drop_confirm.platform

//? if >=1.20.1 {
import net.minecraft.client.gui./*$ gui_graphics_type {*/GuiGraphics/*$}*/ as PoseStack
import xyz.pupbrained.drop_confirm.platform.impl.GuiGraphicsRenderImpl
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
import xyz.pupbrained.drop_confirm.platform.impl.PoseStackRenderImpl
*///?} else {
/*import xyz.pupbrained.drop_confirm.platform.impl.LegacyRenderImpl
*///?}

//? if >=1.16.5
import net.minecraft.util.FormattedCharSequence

import net.minecraft.client.gui.Font

/**
 * Platform-independent rendering interface
 */
interface RenderInterface {
  fun drawString(font: Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean = true): RenderInterface
  fun drawCenteredString(font: Font, text: String, x: Int, y: Int, color: Int): RenderInterface

  //? if >1.16.5
  fun drawCenteredString(font: Font, text: FormattedCharSequence, x: Int, y: Int, color: Int): RenderInterface

  fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Int): RenderInterface
  fun fillGradient(x1: Int, y1: Int, x2: Int, y2: Int, colorStart: Int, colorEnd: Int): RenderInterface

  companion object {
    // @formatter:off
    //? if <=1.15.2
    /*@Suppress("UNUSED_PARAMETER")*/
    fun getRenderImpl(context: Any? = null): RenderInterface =
      //? if >=1.20.1 {
      GuiGraphicsRenderImpl(context as PoseStack)
      //?} else if >=1.16.5 {
      /*PoseStackRenderImpl(context as PoseStack)
      *///?} else {
      /*LegacyRenderImpl()
      *///?}
    // @formatter:on
  }
}
