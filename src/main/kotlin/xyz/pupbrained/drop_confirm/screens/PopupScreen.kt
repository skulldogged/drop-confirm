package xyz.pupbrained.drop_confirm.screens

//? if >=1.20.1 {
import net.minecraft.client.gui./*$ gui_graphics_type {*/GuiGraphics/*$}*/ as PoseStack
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
*///?}

//? if >=1.16.5 {
import net.minecraft.network.chat.Component as Text
//?} else {
/*import kotlin.String as Text
*///?}

//? if fabric {
import net.minecraft.client.gui.components./*? if <=1.18.2 {*//*Widget as*//*?}*/ Renderable
//?}

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.ItemStack
import xyz.pupbrained.drop_confirm.DropConfirm
import xyz.pupbrained.drop_confirm.platform.RenderInterface.Companion.getRenderImpl
import xyz.pupbrained.drop_confirm.util.Color.*
import xyz.pupbrained.drop_confirm.util.ComponentUtils

class PopupScreen(val itemStack: ItemStack) : Screen(ComponentUtils.translatable("gui.drop_confirm")) {
  // Positioning
  private val x1: Int get() = (width - popupWidth) / 2
  private val y1: Int get() = (height - popupHeight) / 2
  private val x2: Int get() = x1 + popupWidth
  private val y2: Int get() = y1 + popupHeight
  private val titleY: Int get() = y1 + titleBarHeight
  private val centerX: Int get() = x1 + popupWidth / 2

  // UI dimensions
  private val popupWidth = 220
  private val popupHeight = 120
  private val titleBarHeight = 25

  //? if fabric
  private val renderables: MutableList<Renderable> = mutableListOf()

  override fun shouldCloseOnEsc() = true
  override fun isPauseScreen() = false

  override fun onClose() {
    DropConfirm.isConfirmed = false
    super.onClose()
  }

  inner class StyledButton(
    x: Int, y: Int, width: Int, height: Int,
    component: Text,
    onPress: OnPress,
    private val baseColor: Int,
    private val hoverColor: Int
  ) : Button(x, y, width, height, component, onPress/*? if >=1.19.4 {*/, DEFAULT_NARRATION/*?}*/) {
    override fun /*$ render_method {*/renderWidget/*$}*/(
      /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
      mouseX: Int,
      mouseY: Int,
      partialTick: Float
    ) {
      isHovered = mouseX in x until (x + width) && mouseY in y until (y + height)

      val color = if (isHovered) hoverColor else baseColor

      getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/).apply {
        fill(x, y + 1, x + width, y + height - 1, color)
        fill(x + 1, y, x + width - 1, y + height, color)

        drawCenteredString(
          font,
          message/*? if >=1.16.5 {*/.string/*?}*/,
          x + width / 2,
          y + (height - 8) / 2,
          TEXT()
        )
      }
    }
  }

  override fun init() {
    super.init()

    renderables.clear()

    // Use the pre-calculated positions
    with(renderables) {
      add(
        StyledButton(
          centerX - 70, y2 - 35,
          60, 20,
          ComponentUtils.translatable("gui.yes")/*? if <=1.15.2 {*//*.string*//*?}*/,
          {
            minecraft?.setScreen(null)

            val player = minecraft?.player ?: return@StyledButton

            val entireStack = minecraft?.options?.keyDrop?.isDown == true

            DropConfirm.isConfirmed = true
            player.swing(player.usedItemHand/*? if >=1.16.5 {*/, true/*?}*/)
            player.drop(entireStack)
          },
          BUTTON_CONFIRM(),
          BUTTON_CONFIRM_HOVER()
        )
      )
      add(
        StyledButton(
          centerX + 10,
          y2 - 35,
          60,
          20,
          ComponentUtils.translatable("gui.no")/*? if <=1.15.2 {*//*.string*//*?}*/,
          { onClose() },
          BUTTON_CANCEL(),
          BUTTON_CANCEL_HOVER()
        )
      )
    }
  }

  override fun /*$ screen_render_fn {*/render/*$}*/(/*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/ mouseX: Int, mouseY: Int, partialTick: Float) {
    getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/).apply {
      //? if <=1.20.1 {
      /*fillGradient(0, 0, width, height, DIMMING(), DIMMING())
      *///?} else if 1.20.4 {
      /*renderTransparentBackground(poseStack)
      *///?} else if <1.21.6 {
      renderBlurredBackground(/*? if <=1.21.1 {*//*partialTick*//*?}*/)
      //?}

      // Border
      fill(x1 + 1, y1, x2 - 1, y1 + 1, BORDER())
      fill(x1, y1 + 1, x1 + 1, y2 - 1, BORDER())
      fill(x2 - 1, y1 + 1, x2, y2 - 1, BORDER())
      fill(x1 + 1, y2 - 1, x2 - 1, y2, BORDER())

      // Cutouts
      fill(x1, y1, x1 + 1, y1 + 1, TRANSPARENT())
      fill(x2 - 1, y1, x2, y1 + 1, TRANSPARENT())
      fill(x1, y2 - 1, x1 + 1, y2, TRANSPARENT())
      fill(x2 - 1, y2 - 1, x2, y2, TRANSPARENT())

      // Content
      fillGradient(x1 + 1, y1 + 1, x2 - 1, titleY, TITLE_BAR_PRIMARY(), TITLE_BAR_SECONDARY())
      fillGradient(x1 + 1, titleY + 1, x2 - 1, y2 - 1, CONTENT_PRIMARY(), CONTENT_SECONDARY())
      fill(x1 + 1, titleY, x2 - 1, titleY + 1, SEPARATOR())

      // Add decorative diagonal elements in corners
      fun drawCornerDecoration(cornerX: Int, cornerY: Int) =
        fill(cornerX, cornerY, cornerX + 1, cornerY + 1, CORNER_DECORATION())

      for (i in 0..3) {
        drawCornerDecoration(x1 + 3 + i, y1 + 3 + i) // Top left
        drawCornerDecoration(x2 - 4 - i, y1 + 3 + i) // Top right
        drawCornerDecoration(x1 + 3 + i, y2 - 4 - i) // Bottom left
        drawCornerDecoration(x2 - 4 - i, y2 - 4 - i) // Bottom right
      }

      drawString(
        font,
        title.string,
        centerX - font.width(title.string) / 2,
        y1 + 9,
        TEXT(),
        true
      )

      val itemName =
        ComponentUtils
          .literal(itemStack.item.getName(itemStack).string)
          .withStyle(/*$ item_style {*/itemStack.rarity.color()/*$}*/)

      //? if <=1.15.2 {
      /*val message = ComponentUtils.translatable("drop_confirm.confirmation.popup", itemName)
      val formattedText = message.coloredString
      *///?} else {
      val formattedText = ComponentUtils.translatable("drop_confirm.confirmation.popup", itemName)
      //?}

      val wrappedText = font.split(formattedText, popupWidth - 40)

      val lineSpacing = (font.lineHeight * 0.5).toInt()
      val totalTextHeight = wrappedText.size * font.lineHeight + (wrappedText.size - 1) * lineSpacing

      val topY = titleY + 10
      val buttonY = y2 - 35
      val bottomY = buttonY - 10
      val availableHeight = bottomY - topY

      val startY = topY + (availableHeight - totalTextHeight) / 2

      wrappedText.forEachIndexed { i, text ->
        //? if >=1.19.4 {
        drawCenteredString(
          font,
          text,
          centerX,
          startY + i * (font.lineHeight + lineSpacing),
          TEXT()
        )
        //?} else {
        /*font.drawShadow(
          /^? if >=1.16.5 {^/poseStack,/^?}^/
          text,
          (centerX - font.width(text) / 2).toFloat(),
          (startY + i * (font.lineHeight + lineSpacing)).toFloat(),
          TEXT()
        )
        *///?}
      }
    }

    renderables.forEach { it./*$ screen_render_fn {*/render/*$}*/(/*? if >=1.16.5 {*/poseStack,/*?}*/ mouseX, mouseY, partialTick) }
  }

  //? if >=1.21.9 {
  /*override fun mouseClicked(event: net.minecraft.client.input.MouseButtonEvent, focus: Boolean): Boolean =
    when {
      // Case 1: If meets Screen's conditions
      super.mouseClicked(event, focus) -> true

      // Case 2: If any button is clicked
      renderables.filterIsInstance<Button>()
        .any { it.mouseClicked(event, focus) } -> true

      // Case 3: If mouse is outside the popup area and should close on ESC
      (event.x() < x1 || event.x() >= x2 || event.y() < y1 || event.y() >= y2) && shouldCloseOnEsc() -> {
        minecraft?.setScreen(null)
        true
      }

      // Case 4: Otherwise
      else -> false
    }
  *///?} else {
  override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int) =
    when {
      // Case 1: If meets Screen's conditions
      super.mouseClicked(mouseX, mouseY, button) -> true

      // Case 2: If any button is clicked
      renderables.filterIsInstance<Button>()
        .any { it.mouseClicked(mouseX, mouseY, button) } -> true

      // Case 3: If mouse is outside the popup area and should close on ESC
      (mouseX < x1 || mouseX >= x2 || mouseY < y1 || mouseY >= y2) && shouldCloseOnEsc() -> {
        minecraft?.setScreen(null)
        true
      }

      // Case 4: Otherwise
      else -> false
    }
  //?}
}
