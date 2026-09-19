package dev.skulldogged.drop_confirm.config.screens

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.Item
import dev.skulldogged.drop_confirm.config.DropConfirmConfig
import dev.skulldogged.drop_confirm.config.widgets.ItemList
import dev.skulldogged.drop_confirm.config.widgets.TextInput
import dev.skulldogged.drop_confirm.config.widgets.VanillaWidgets
import dev.skulldogged.drop_confirm.platform.RenderInterface
import dev.skulldogged.drop_confirm.util.ClientGuiUtils
import dev.skulldogged.drop_confirm.util.Color
import dev.skulldogged.drop_confirm.util.ItemUtils

/**
 * Editor for the item blacklist / whitelist.
 *
 * Changes are kept in a working copy and written to the config when leaving the screen.
 */
class DropConfirmListEditorScreen(parent: Screen?) : ConfigScreenBase(
  if (DropConfirmConfig.treatAsWhitelist) "option.drop_confirm.whitelisted_items" else "option.drop_confirm.blacklisted_items",
  parent
) {
  private val items: MutableList<Item> = DropConfirmConfig.blacklistedItems.toMutableList()

  private lateinit var list: ItemList
  private lateinit var input: TextInput
  private lateinit var addButton: Button

  /** Text in the input box, preserved across re-initialisation (e.g. window resize). */
  private var pendingText = ""

  private var status = ""
  private var statusColor = Color.ERROR

  private val listTop: Int
    get() = CONTENT_TOP

  private val listBottom: Int
    get() = height - CONTROL_HEIGHT - MARGIN - STATUS_HEIGHT - MARGIN

  override fun buildWidgets() {
    val rowY = height - CONTROL_HEIGHT - MARGIN

    list = addList(ItemList(Minecraft.getInstance(), 0, listTop, width, listBottom - listTop, t("option.drop_confirm.remove")) {
      removeItem(it)
    })
    list.setItems(items)

    add(VanillaWidgets.button(MARGIN, rowY, BACK_WIDTH, CONTROL_HEIGHT, t("gui.back")) { saveAndReturn() })

    val inputX = MARGIN + BACK_WIDTH + BUTTON_SPACING
    val inputWidth = width - MARGIN * 2 - BACK_WIDTH - ADD_WIDTH - BUTTON_SPACING * 2

    input = add(TextInput(font, inputX, rowY, inputWidth, CONTROL_HEIGHT, t("option.drop_confirm.enter_item_id")) {
      addFromInput()
    })
    input.value = pendingText
    input.setResponder {
      pendingText = it
      refreshInputState()
    }

    addButton = add(VanillaWidgets.button(width - MARGIN - ADD_WIDTH, rowY, ADD_WIDTH, CONTROL_HEIGHT, t("option.drop_confirm.add")) {
      addFromInput()
    })

    refreshInputState()
  }

  //? if <=1.20.1 {
  /*override fun tick() {
    super.tick()
    input.tick()
  }
  *///?}

  override fun drawExtra(render: RenderInterface, mouseX: Int, mouseY: Int) {
    if (items.isEmpty())
      render.drawCenteredString(font, t("option.drop_confirm.list_empty"), width / 2, (listTop + listBottom) / 2 - 4, Color.DISABLED())

    if (status.isEmpty()) return

    val statusY = height - CONTROL_HEIGHT - MARGIN - STATUS_HEIGHT + (STATUS_HEIGHT - 8) / 2
    render.drawCenteredString(font, status, width / 2, statusY, statusColor())
  }

  private fun setStatus(message: String, color: Color = Color.ERROR) {
    status = message
    statusColor = color
  }

  private fun refreshInputState() {
    val text = pendingText.trim()

    if (text.isEmpty()) {
      input.setTextColor(Color.TEXT())
      addButton.active = false
      setStatus("")
      return
    }

    val item = ItemUtils.lookup(text)
    when {
      item == null -> {
        input.setTextColor(Color.ERROR())
        addButton.active = false
        setStatus(t("option.drop_confirm.invalid_item"))
      }

      item in items -> {
        input.setTextColor(Color.ERROR())
        addButton.active = false
        setStatus(t("option.drop_confirm.already_listed"))
      }

      else -> {
        input.setTextColor(Color.SUCCESS())
        addButton.active = true
        setStatus("")
      }
    }
  }

  private fun addFromInput() {
    val item = ItemUtils.lookup(pendingText) ?: return
    if (item in items) return

    items.add(item)
    list.setItems(items)
    list.scrollToBottom()

    input.value = ""
    pendingText = ""
    refreshInputState()
    setStatus(t("option.drop_confirm.item_added", ItemUtils.displayName(item)), Color.SUCCESS)
  }

  private fun removeItem(item: Item) {
    if (!items.remove(item)) return

    list.setItems(items)
    refreshInputState()
  }

  private fun saveAndReturn() {
    DropConfirmConfig.blacklistedItems = items
    DropConfirmConfig.save()
    ClientGuiUtils.setScreen(minecraft, parent)
  }

  /** Escape saves like the Back button; the list is only ever edited deliberately. */
  override fun onClose() = saveAndReturn()

  companion object {
    private const val MARGIN = 8
    private const val BUTTON_SPACING = 6
    private const val BACK_WIDTH = 80
    private const val ADD_WIDTH = 100
    private const val STATUS_HEIGHT = 14
  }
}
