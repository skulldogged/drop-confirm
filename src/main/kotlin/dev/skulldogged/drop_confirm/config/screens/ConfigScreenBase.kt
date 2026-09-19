package dev.skulldogged.drop_confirm.config.screens

//? if >=1.20.1 {
import net.minecraft.client.gui./*$ gui_graphics_type {*/GuiGraphics/*$}*/ as PoseStack
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
*///?}

//? if >=1.19.4 {
import net.minecraft.client.gui.components.Tooltip
//?} else {
/*import net.minecraft.client.gui.components.Widget
*///?}

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.language.I18n
import dev.skulldogged.drop_confirm.config.widgets.ItemList
import dev.skulldogged.drop_confirm.platform.RenderInterface
import dev.skulldogged.drop_confirm.platform.RenderInterface.Companion.getRenderImpl
import dev.skulldogged.drop_confirm.util.ClientGuiUtils
import dev.skulldogged.drop_confirm.util.Color
import dev.skulldogged.drop_confirm.util.ComponentUtils

/**
 * Shared behaviour for the mod's settings screens: widget registration, the title,
 * tooltips and returning to the parent screen. Everything is drawn with vanilla widgets.
 */
abstract class ConfigScreenBase(titleKey: String, protected val parent: Screen?) :
  Screen(ComponentUtils.translatable(titleKey)) {

  //? if <1.19.4 {
  /*private val legacyTooltips = mutableMapOf<AbstractWidget, String>()
  *///?}
  //? if <1.17.1 {
  /*private val legacyLists = mutableListOf<Widget>()
  *///?}

  /** Registers a widget for rendering and input. */
  protected fun <T : AbstractWidget> add(widget: T): T = /*$ add_widget_fn {*/addRenderableWidget/*$}*/(widget)

  /** Registers a selection list for rendering and input. */
  protected fun addList(list: ItemList): ItemList {
    //? if >=1.17.1 {
    return addRenderableWidget(list)
    //?} else {
    /*children.add(list)
    legacyLists.add(list)
    return list
    *///?}
  }

  /** Attaches a tooltip to a widget, using the vanilla tooltip system where it exists. */
  protected fun <T : AbstractWidget> T.withTooltip(text: String): T {
    //? if >=1.19.4 {
    setTooltip(Tooltip.create(ComponentUtils.literal(text)))
    //?} else {
    /*legacyTooltips[this] = text
    *///?}
    return this
  }

  /** Creates this screen's widgets. Called from [init], including after a resize. */
  protected abstract fun buildWidgets()

  /** Hook for drawing extra content on top of the widgets. */
  protected open fun drawExtra(render: RenderInterface, mouseX: Int, mouseY: Int) {}

  override fun init() {
    super.init()
    //? if <1.19.4
    /*legacyTooltips.clear()*/
    //? if <1.17.1
    /*legacyLists.clear()*/
    buildWidgets()
  }

  override fun /*$ screen_render_fn {*/render/*$}*/(
    /*? if >=1.16.5 {*/context: PoseStack,/*?}*/
    mouseX: Int,
    mouseY: Int,
    partialTick: Float
  ) {
    //? if <=1.20.1
    /*renderBackground(/^? if >=1.16.5 {^/context/^?}^/)*/
    //? if <1.17.1
    /*legacyLists.forEach { it.render(/^? if >=1.16.5 {^/context, /^?}^/mouseX, mouseY, partialTick) }*/

    super./*$ screen_render_fn {*/render/*$}*/(/*? if >=1.16.5 {*/context, /*?}*/mouseX, mouseY, partialTick)

    val render = getRenderImpl(/*? if >=1.16.5 {*/context/*?}*/)
    render.drawCenteredString(font, title.string, width / 2, TITLE_Y, Color.TEXT())
    drawExtra(render, mouseX, mouseY)

    //? if <1.19.4 {
    /*val hovered = legacyTooltips.entries.firstOrNull { it.key.isMouseOver(mouseX.toDouble(), mouseY.toDouble()) }
    if (hovered != null) {
      //? if >=1.16.5 {
      renderTooltip(context, font.split(ComponentUtils.literal(hovered.value), TOOLTIP_WIDTH), mouseX, mouseY)
      //?} else {
      /^renderTooltip(font.split(hovered.value, TOOLTIP_WIDTH), mouseX, mouseY)
      ^///?}
    }
    *///?}
  }

  override fun onClose() = ClientGuiUtils.setScreen(minecraft, parent)

  companion object {
    const val CONTROL_HEIGHT = 20
    const val CONTROL_SPACING = 6
    const val TITLE_Y = 12
    const val CONTENT_TOP = TITLE_Y + 20
    private const val TOOLTIP_WIDTH = 200

    /** Translates [key] with the client's current language. */
    fun t(key: String, vararg args: Any): String = I18n.get(key, *args)
  }
}
