package dev.skulldogged.drop_confirm.config.widgets

//? if >=1.20.1 {
import net.minecraft.client.gui./*$ gui_graphics_type {*/GuiGraphics/*$}*/ as PoseStack
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
*///?}

//? if >=1.17.1
import net.minecraft.client.gui.narration.NarratableEntry

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.world.item.Item
import dev.skulldogged.drop_confirm.platform.RenderInterface
import dev.skulldogged.drop_confirm.platform.RenderInterface.Companion.getRenderImpl
import dev.skulldogged.drop_confirm.util.Color
import dev.skulldogged.drop_confirm.util.ItemUtils

/**
 * A vanilla scrolling list where each row shows an item's icon and name with a Remove button.
 */
class ItemList(
  minecraft: Minecraft,
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  private val removeLabel: String,
  private val onRemove: (Item) -> Unit
) : ContainerObjectSelectionList<ItemList.Entry>(
  minecraft,
  width,
  height,
  /*? if >=1.20.4 {*/y,/*?} else {*//*y, y + height,*//*?}*/
  ROW_HEIGHT
) {
  init {
    //? if >=1.20.4 {
    setX(x)
    //?} else {
    /*setLeftPos(x)
    *///?}
  }

  /** Replaces the rows with one entry per item. */
  fun setItems(items: List<Item>) {
    clearEntries()
    items.forEach { addEntry(Entry(it)) }
  }

  fun scrollToBottom() = setScrollAmount(Double.MAX_VALUE)

  override fun getRowWidth(): Int = width - ROW_MARGIN * 2

  //? if >=1.21.4 {
  override fun scrollBarX(): Int = x + width - SCROLLBAR_MARGIN
  //?} else {
  /*override fun getScrollbarPosition(): Int = /^? if >=1.20.4 {^/x/^?} else {^//^x0^//^?}^/ + width - SCROLLBAR_MARGIN
  *///?}

  inner class Entry(val item: Item) : ContainerObjectSelectionList.Entry<Entry>() {
    private val button: Button =
      VanillaWidgets.button(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, removeLabel) { onRemove(item) }

    override fun children(): List<GuiEventListener> = listOf(button)

    //? if >=1.17.1
    override fun narratables(): List<NarratableEntry> = listOf(button)

    private fun drawRow(render: RenderInterface, top: Int, left: Int, rowWidth: Int, rowHeight: Int) {
      ItemUtils.displayStack(item)?.let { render.drawItem(it, left + ICON_PADDING, top + (rowHeight - ICON_SIZE) / 2) }

      render.drawString(
        Minecraft.getInstance().font,
        ItemUtils.displayName(item),
        left + ICON_PADDING + ICON_SIZE + ICON_PADDING,
        top + (rowHeight - 8) / 2,
        Color.TEXT()
      )

      button.x = left + rowWidth - BUTTON_WIDTH - BUTTON_MARGIN
      button.y = top + (rowHeight - BUTTON_HEIGHT) / 2
    }

    //? if >=26.1 {
    /*override fun extractContent(context: PoseStack, mouseX: Int, mouseY: Int, hovered: Boolean, partialTick: Float) {
      drawRow(getRenderImpl(context), contentY, contentX, contentWidth, contentHeight)
      button.extractRenderState(context, mouseX, mouseY, partialTick)
    }
    *///?} elif >=1.21.9 {
    /*override fun renderContent(context: PoseStack, mouseX: Int, mouseY: Int, hovered: Boolean, partialTick: Float) {
      drawRow(getRenderImpl(context), contentY, contentX, contentWidth, contentHeight)
      button.render(context, mouseX, mouseY, partialTick)
    }
    *///?} else {
    override fun render(
      /*? if >=1.16.5 {*/context: PoseStack,/*?}*/
      index: Int,
      top: Int,
      left: Int,
      rowWidth: Int,
      rowHeight: Int,
      mouseX: Int,
      mouseY: Int,
      hovered: Boolean,
      partialTick: Float
    ) {
      drawRow(getRenderImpl(/*? if >=1.16.5 {*/context/*?}*/), top, left, rowWidth, rowHeight)
      button.render(/*? if >=1.16.5 {*/context, /*?}*/mouseX, mouseY, partialTick)
    }
    //?}
  }

  companion object {
    const val ROW_HEIGHT = 24
    private const val ROW_MARGIN = 20
    private const val SCROLLBAR_MARGIN = 10
    private const val ICON_SIZE = 16
    private const val ICON_PADDING = 4
    private const val BUTTON_WIDTH = 60
    private const val BUTTON_HEIGHT = 20
    private const val BUTTON_MARGIN = 2
  }
}
