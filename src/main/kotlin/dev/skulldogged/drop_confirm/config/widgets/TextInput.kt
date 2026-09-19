package dev.skulldogged.drop_confirm.config.widgets

//? if >=1.21.9 {
/*import net.minecraft.client.input.KeyEvent
*///?} else {
import org.lwjgl.glfw.GLFW
//?}

//? if <1.19.4 {
/*//? if >=1.16.5 {
import com.mojang.blaze3d.vertex.PoseStack
//?}
import net.minecraft.client.Minecraft
import dev.skulldogged.drop_confirm.platform.RenderInterface.Companion.getRenderImpl
import dev.skulldogged.drop_confirm.util.Color
*///?}

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.EditBox
import dev.skulldogged.drop_confirm.util.ComponentUtils

/**
 * A vanilla text box with placeholder text and an Enter callback.
 */
class TextInput(
  font: Font,
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  private val placeholder: String,
  private val onEnter: () -> Unit
) : EditBox(font, x, y, width, height, /*? if >=1.16.5 {*/ComponentUtils.literal(placeholder)/*?} else {*//*placeholder*//*?}*/) {
  init {
    setMaxLength(256)
    //? if >=1.19.4
    setHint(ComponentUtils.literal(placeholder))
  }

  //? if <1.19.4 {
  /*override fun renderButton(/^? if >=1.16.5 {^/context: PoseStack, /^?}^/mouseX: Int, mouseY: Int, partialTick: Float) {
    super.renderButton(/^? if >=1.16.5 {^/context, /^?}^/mouseX, mouseY, partialTick)

    if (value.isEmpty() && !isFocused())
      getRenderImpl(/^? if >=1.16.5 {^/context/^?}^/).drawString(
        Minecraft.getInstance().font,
        placeholder,
        x + 4,
        y + (height - 8) / 2,
        Color.PLACEHOLDER(),
        false
      )
  }
  *///?}

  //? if >=1.21.9 {
  /*override fun keyPressed(event: KeyEvent): Boolean {
    if (isFocused() && event.isConfirmation()) {
      onEnter()
      return true
    }

    return super.keyPressed(event)
  }
  *///?} else {
  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
    if (isFocused() && (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
      onEnter()
      return true
    }

    return super.keyPressed(keyCode, scanCode, modifiers)
  }
  //?}
}
