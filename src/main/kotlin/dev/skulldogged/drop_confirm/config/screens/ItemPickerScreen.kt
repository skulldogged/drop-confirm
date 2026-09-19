package dev.skulldogged.drop_confirm.config.screens

//? if >=1.20.1 {
import net.minecraft.client.gui./*$ gui_graphics_type {*/GuiGraphics/*$}*/ as PoseStack
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
*///?}

//? if <=1.18.2 {
/*import net.minecraft.core.Registry as BuiltInRegistries
*///?} else {
import net.minecraft.core.registries.BuiltInRegistries
//?}

//? if >=26.1 {
/*import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
*///?}

import java.util.Locale
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import dev.skulldogged.drop_confirm.config.ExactItem
import dev.skulldogged.drop_confirm.config.widgets.TextInput
import dev.skulldogged.drop_confirm.config.widgets.VanillaWidgets
import dev.skulldogged.drop_confirm.platform.RenderInterface
import dev.skulldogged.drop_confirm.platform.RenderInterface.Companion.getRenderImpl
import dev.skulldogged.drop_confirm.util.Color
import dev.skulldogged.drop_confirm.util.ComponentUtils
import dev.skulldogged.drop_confirm.util.ItemUtils

/**
 * A searchable two-column item picker: every registered item on the left, the
 * chosen items on the right. Exact stacks added with the keybind are shown in the
 * right column too, so they can be removed here.
 *
 * Edits a local draft; [apply] receives the result when Done is pressed and the
 * parent screen remains responsible for saving.
 */
class ItemPickerScreen(
  parent: Screen,
  initialItems: List<Item>,
  initialExact: List<ExactItem>,
  private val apply: (List<Item>, List<ExactItem>) -> Unit
) : ConfigScreenBase("item_picker.drop_confirm.title", parent) {
  /** One row in either column. */
  private sealed class Entry {
    class Type(val item: Item) : Entry()
    class Exact(val entry: ExactItem) : Entry()
  }

  private val selected = initialItems.filter { it !== Items.AIR }.toMutableSet()
  private val selectedExact = initialExact.toMutableList()

  private val catalog: List<Item> = BuiltInRegistries.ITEM.filter { it !== Items.AIR }
    .sortedWith(compareBy<Item> { nameOf(it).lowercase(Locale.ROOT) }.thenBy { ItemUtils.idOf(it) })

  private var query = ""
  private var availableOffset = 0
  private var selectedOffset = 0
  private var available = emptyList<Item>()
  private var chosen = emptyList<Entry>()
  private val rows = mutableListOf<Button>()

  private lateinit var search: TextInput
  private lateinit var availableUp: Button
  private lateinit var availableDown: Button
  private lateinit var selectedUp: Button
  private lateinit var selectedDown: Button

  private val contentWidth get() = minOf(width - 24, 680)
  private val left get() = (width - contentWidth) / 2
  private val columnWidth get() = (contentWidth - COLUMN_GAP) / 2
  private val right get() = left + columnWidth + COLUMN_GAP
  private val visibleRows get() = maxOf(1, (height - LIST_TOP - 54) / ROW_HEIGHT)
  private val listBottom get() = LIST_TOP + visibleRows * ROW_HEIGHT

  private fun text(key: String, vararg args: Any) = t("item_picker.drop_confirm.$key", *args)

  override fun buildWidgets() {
    rows.clear()

    search = add(TextInput(font, (width - columnWidth) / 2, SEARCH_Y, columnWidth, CONTROL_HEIGHT, text("search")) {})
    search.value = query
    search.setResponder {
      query = it
      availableOffset = 0
      refresh()
    }

    fun pager(x: Int, delta: Int, selectedColumn: Boolean): Button =
      add(VanillaWidgets.button(x, listBottom + 4, PAGER_WIDTH, PAGER_HEIGHT, if (delta < 0) "<" else ">") {
        if (selectedColumn) selectedOffset += delta * visibleRows else availableOffset += delta * visibleRows
        refresh()
      }.withTooltip(text(if (delta < 0) "previous" else "next")))

    availableUp = pager(left, -1, false)
    availableDown = pager(left + columnWidth - PAGER_WIDTH, 1, false)
    selectedUp = pager(right, -1, true)
    selectedDown = pager(right + columnWidth - PAGER_WIDTH, 1, true)

    val groupX = width / 2 - (CONTROL_WIDTH * 2 + BUTTON_SPACING) / 2
    val buttonY = height - CONTROL_HEIGHT - BUTTON_SPACING

    add(VanillaWidgets.button(groupX, buttonY, CONTROL_WIDTH, CONTROL_HEIGHT, t("gui.cancel")) { onClose() })
    add(VanillaWidgets.button(groupX + CONTROL_WIDTH + BUTTON_SPACING, buttonY, CONTROL_WIDTH, CONTROL_HEIGHT, t("gui.done")) {
      apply(selected.toList(), selectedExact.toList())
      onClose()
    })

    refresh()

    //? if >=1.19.4 {
    setInitialFocus(search)
    //?} else {
    /*focused = search
    search.setFocus(true)
    *///?}
  }

  //? if <=1.20.1 {
  /*override fun tick() {
    super.tick()
    search.tick()
  }
  *///?}

  private fun refresh() {
    val filter = query.trim().lowercase(Locale.ROOT)

    available = catalog.filter {
      it !in selected && (filter.isEmpty() ||
        nameOf(it).lowercase(Locale.ROOT).contains(filter) ||
        ItemUtils.idOf(it).contains(filter))
    }
    chosen = catalog.filter { it in selected }.map<Item, Entry> { Entry.Type(it) } +
      selectedExact.map { Entry.Exact(it) }

    availableOffset = availableOffset.coerceIn(0, maxOf(0, available.size - visibleRows))
    selectedOffset = selectedOffset.coerceIn(0, maxOf(0, chosen.size - visibleRows))

    // Keep the search box and pagers; only the rows change.
    val focusedRow = rows.indexOf(focused)
    rows.forEach { remove(it) }
    rows.clear()

    fun addRows(entries: List<Entry>, offset: Int, x: Int, removing: Boolean) {
      entries.drop(offset).take(visibleRows).forEachIndexed { index, entry ->
        rows.add(add(PickerRow(entry, removing, x, LIST_TOP + index * ROW_HEIGHT)))
      }
    }

    addRows(available.map { Entry.Type(it) }, availableOffset, left, false)
    addRows(chosen, selectedOffset, right, true)

    if (focusedRow >= 0) focused = rows.getOrNull(focusedRow.coerceAtMost(rows.lastIndex)) ?: search

    availableUp.active = availableOffset > 0
    availableDown.active = availableOffset + visibleRows < available.size
    selectedUp.active = selectedOffset > 0
    selectedDown.active = selectedOffset + visibleRows < chosen.size
  }

  private fun toggle(entry: Entry) {
    when (entry) {
      is Entry.Type -> if (!selected.remove(entry.item)) selected.add(entry.item)
      is Entry.Exact -> selectedExact.remove(entry.entry)
    }

    // Replacing rows during a click can dispatch that click to the new row, so defer.
    minecraft?./*$ schedule_task {*/schedule/*$}*/ { refresh() }
  }

  private fun trimLabel(value: String, maxWidth: Int): String =
    if (font.width(value) <= maxWidth) value
    else font./*? if >=1.16.5 {*/plainSubstrByWidth/*?} else {*//*substrByWidth*//*?}*/(value, maxWidth - font.width(ELLIPSIS)) + ELLIPSIS

  override fun drawBackdrop(render: RenderInterface) {
    render.fill(left, LIST_TOP, left + columnWidth, listBottom, Color.PICKER_COLUMN())
    render.fill(right, LIST_TOP, right + columnWidth, listBottom, Color.PICKER_COLUMN())
  }

  override fun drawExtra(render: RenderInterface, mouseX: Int, mouseY: Int) {
    render.drawString(font, text("available", available.size), left, HEADER_Y, Color.TEXT())
    render.drawString(font, text("selected", chosen.size), right, HEADER_Y, Color.TEXT())

    if (available.isEmpty())
      render.drawCenteredString(font, text("no_matches"), left + columnWidth / 2, LIST_TOP + 12, Color.DISABLED())
    if (chosen.isEmpty())
      render.drawCenteredString(font, text("empty"), right + columnWidth / 2, LIST_TOP + 12, Color.DISABLED())

    fun range(offset: Int, size: Int) =
      if (size == 0) "0 / 0" else "${offset + 1}-${minOf(offset + visibleRows, size)} / $size"

    render.drawCenteredString(font, range(availableOffset, available.size), left + columnWidth / 2, listBottom + 9, Color.DISABLED())
    render.drawCenteredString(font, range(selectedOffset, chosen.size), right + columnWidth / 2, listBottom + 9, Color.DISABLED())
  }

  //? if >=1.20.4 {
  override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontal: Double, vertical: Double): Boolean =
    scroll(mouseX, mouseY, vertical)
  //?} else {
  /*override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean = scroll(mouseX, mouseY, delta)
  *///?}

  private fun scroll(mouseX: Double, mouseY: Double, delta: Double): Boolean {
    if (mouseY < LIST_TOP || mouseY >= listBottom || delta == 0.0) return false

    val movement = if (delta > 0) -SCROLL_STEP else SCROLL_STEP
    when {
      mouseX >= left && mouseX < left + columnWidth -> availableOffset += movement
      mouseX >= right && mouseX < right + columnWidth -> selectedOffset += movement
      else -> return false
    }

    refresh()
    return true
  }

  /** One row: item icon, name and ID, with a plus or minus marker. Pressing it moves the entry. */
  private inner class PickerRow(private val entry: Entry, private val removing: Boolean, x: Int, y: Int) :
    Button(
      x, y, columnWidth, ROW_HEIGHT - 2,
      ComponentUtils.translatable("item_picker.drop_confirm.${if (removing) "remove_item" else "add_item"}", labelOf(entry))
        /*? if <=1.15.2 {*//*.string*//*?}*/,
      { toggle(entry) }
      /*? if >=1.19.4 {*/, DEFAULT_NARRATION/*?}*/
    ) {
    private val name: String = labelOf(entry)

    private val detail: String = when (entry) {
      is Entry.Type -> ItemUtils.idOf(entry.item)
      is Entry.Exact -> text("exact", entry.entry.id)
    }

    private val preview: ItemStack? = when (entry) {
      is Entry.Type -> previewOf(entry.item)
      is Entry.Exact -> ItemUtils.lookup(entry.entry.id)?.let { previewOf(it) }
    }

    override fun /*$ render_method {*/renderWidget/*$}*/(
      /*? if >=1.16.5 {*/context: PoseStack,/*?}*/
      mouseX: Int,
      mouseY: Int,
      partialTick: Float
    ) {
      val hover = isMouseOver(mouseX.toDouble(), mouseY.toDouble()) || isFocused()
      val render = getRenderImpl(/*? if >=1.16.5 {*/context/*?}*/)

      render.fill(x, y, x + width, y + height, (if (hover) Color.PICKER_ROW_HOVER else Color.PICKER_ROW)())
      if (hover) render.fill(x, y, x + 2, y + height, Color.TEXT())

      val maxWidth = width - 48
      render.drawString(font, trimLabel(name, maxWidth), x + 26, y + 3, Color.TEXT())
      render.drawString(font, trimLabel(detail, maxWidth), x + 26, y + 13, Color.DISABLED())
      render.drawCenteredString(font, if (removing) "-" else "+", x + width - 11, y + 8, Color.TEXT())
      preview?.let { render.drawItem(it, x + 5, y + 4) }
    }
  }

  companion object {
    private const val CONTROL_WIDTH = 150
    private const val BUTTON_SPACING = 8
    private const val COLUMN_GAP = 12
    private const val SEARCH_Y = 44
    private const val HEADER_Y = 70
    private const val LIST_TOP = 82
    private const val ROW_HEIGHT = 26
    private const val PAGER_WIDTH = 22
    private const val PAGER_HEIGHT = 18
    private const val SCROLL_STEP = 3
    private const val ELLIPSIS = "..."

    /** The item's translated name, which works before a world is loaded on every version. */
    private fun nameOf(item: Item): String = ComponentUtils.translatable(item.descriptionId).string

    private fun labelOf(entry: Entry): String = when (entry) {
      is Entry.Type -> nameOf(entry.item)
      is Entry.Exact -> entry.entry.name
    }

    /**
     * A stack for drawing the icon. Before a world is loaded, 26.1+ has item models but no
     * bound default components, so build one with local components instead of touching the registry.
     */
    private fun previewOf(item: Item): ItemStack =
      //? if >=26.1 {
      /*if (!item.builtInRegistryHolder().areComponentsBound()) {
        val components = DataComponentMap.builder()
          .addAll(DataComponents.COMMON_ITEM_COMPONENTS)
          .set(DataComponents.ITEM_NAME, ComponentUtils.translatable(item.descriptionId))
          .set(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(item))
          .build()
        ItemStack(Holder.direct(item, components))
      } else
      *///?}
      ItemStack(item)
  }
}
