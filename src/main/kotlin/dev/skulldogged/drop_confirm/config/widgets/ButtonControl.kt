//? if <1.20.1 || forge {
/*package dev.skulldogged.drop_confirm.config.widgets

//? if >=1.20.1 {
import net.minecraft.client.gui.GuiGraphics as PoseStack
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
*///?}

import com.gitlab.cdagaming.unilib.ModUtils
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl
import dev.skulldogged.drop_confirm.util.Color.*

open class ButtonControl(
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  text: String,
  onPushEvent: () -> Unit,
) : ExtendedButtonControl(x, y, width, height, text, onPushEvent) {
  var textColor = TEXT

  override fun render(
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
    mouseX: Int,
    mouseY: Int,
    partialTicks: Float
  ) {
    if (!isControlVisible) return

    val mc = ModUtils.getMinecraft() ?: return

    isHoveringOver = isOverScreen && RenderUtils.isMouseOver(
      mouseX.toDouble(),
      mouseY.toDouble(),
      this,
    )

    renderBg(/*? if >=1.16.5 {*/poseStack,/*?}*/ mc, mouseX, mouseY)

    val colorToRender = when {
      !isControlEnabled -> DISABLED
      //? if 1.14.4 {
      /*isHoveringOrFocusingOver -> HOVERED
      *///?}
      else -> textColor
    }

    RenderUtils.renderScrollingString(
      /*? if >=1.16.5 {*/poseStack,/*?}*/
      mc,
      mc.font,
      controlMessage,
      left + 2,
      top,
      right - 2,
      bottom,
      colorToRender()
    )
  }
}
*///?}
