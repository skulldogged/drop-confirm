//? if <1.20.1 || forge {
/*package xyz.pupbrained.drop_confirm.config.screens

//? if >=1.20.1 {
import net.minecraft.client.gui.GuiGraphics as PoseStack
import xyz.pupbrained.drop_confirm.platform.impl.GuiGraphicsRenderImpl
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
import xyz.pupbrained.drop_confirm.platform.impl.PoseStackRenderImpl
*///?}

//? if >=1.18.2 {
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
//?}

//? if fabric {
import net.minecraft.core./*? if <=1.18.2 {*//*Registry*//*?} else {*/registries.BuiltInRegistries as Registry/*?}*/
//?} else {
/*import net.minecraftforge.registries.ForgeRegistries as Registry
*///?}

import xyz.pupbrained.drop_confirm.config.widgets.ButtonControl
import com.gitlab.cdagaming.unilib.utils.ItemUtils
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.ScrollableListControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
import org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.util.Color
import xyz.pupbrained.drop_confirm.platform.RenderInterface.Companion.getRenderImpl
import xyz.pupbrained.drop_confirm.util.ClientGuiUtils
import xyz.pupbrained.drop_confirm.util.ComponentUtils

class DropConfirmListEditorScreen(private val parentScreen: Screen) :
  ExtendedScreen(ComponentUtils.translatable("option.drop_confirm.${if (DropConfirmConfig.treatAsWhitelist) "white" else "black"}listed_items").string) {
  private val itemsList: MutableList<Item> = DropConfirmConfig.blacklistedItems.toMutableList()

  private lateinit var itemDisplayList: ItemList
  private lateinit var backButton: ButtonControl
  private lateinit var newItemTextField: ExtendedTextControl
  private lateinit var addButton: ButtonControl
  private lateinit var originalAddButtonText: String

  private val listBottom: Int
    get() = height - LIST_BOTTOM_MARGIN

  private val listHeight: Int
    get() = listBottom - LIST_TOP_MARGIN

  private val bottomControlY: Int
    get() = listBottom + (LIST_BOTTOM_MARGIN - CONTROL_HEIGHT) / 2

  private val backButtonX: Int
    get() = SIDE_MARGIN

  private val addButtonX: Int
    get() = width - ADD_BUTTON_WIDTH - SIDE_MARGIN

  private val textFieldX: Int
    get() = backButtonX + BACK_BUTTON_WIDTH + BUTTON_SPACING

  private val textFieldWidth: Int
    get() = addButtonX - BUTTON_SPACING - textFieldX

  private val textFieldY: Int
    get() = bottomControlY + 1

  companion object {
    private const val CONTROL_HEIGHT = 20
    private const val BUTTON_SPACING = 8
    private const val SIDE_MARGIN = 6
    private const val BACK_BUTTON_WIDTH = 80
    private const val ADD_BUTTON_WIDTH = 120

    private const val LIST_TOP_MARGIN = 32
    private const val LIST_BOTTOM_MARGIN = 38

    private const val TEXT_FIELD_VERTICAL_ADJUST = 2
    private const val TEXT_FIELD_HEIGHT = CONTROL_HEIGHT - TEXT_FIELD_VERTICAL_ADJUST

    const val ENTRY_HEIGHT = 24
    const val ICON_SIZE = 16
    const val ICON_TEXT_PADDING = 4
    const val REMOVE_BUTTON_SIZE = 20
    const val HORIZONTAL_PADDING = 4
    const val BUTTON_RIGHT_MARGIN = 4
  }

  override fun initializeUi() {
    itemDisplayList = ItemList(
      gameInstance,
      this,
      screenWidth,
      listHeight,
      LIST_TOP_MARGIN,
      listBottom,
      itemsList
    )

    backButton = ButtonControl(
      backButtonX,
      bottomControlY,
      BACK_BUTTON_WIDTH,
      CONTROL_HEIGHT,
      ComponentUtils.translatable("gui.back").string
    ) { saveAndReturn() }

    newItemTextField = object : ExtendedTextControl(
      fontRenderer,
      textFieldX,
      textFieldY,
      textFieldWidth,
      TEXT_FIELD_HEIGHT
    ) {
      private val placeholderText = ComponentUtils.translatable("option.drop_confirm.enter_item_id").string

      override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (isFocused && (keyCode == GLFW_KEY_ENTER || keyCode == GLFW_KEY_KP_ENTER)) {
          this@DropConfirmListEditorScreen.addItemFromTextField()
          return true
        }

        return super.keyPressed(keyCode, scanCode, modifiers)
      }

      override fun render(
        /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float
      ) {
        super.render(
          /*? if >=1.16.5 {*/poseStack,/*?}*/
          mouseX,
          mouseY,
          partialTicks
        )

        if (value.isEmpty())
          getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/).drawString(
            font,
            placeholderText,
            x + 4,
            y + (height - 8) / 2,
            0x808080
          )
      }
    }

    originalAddButtonText = ComponentUtils.translatable("option.drop_confirm.add").string
    addButton = ButtonControl(
      addButtonX,
      bottomControlY,
      ADD_BUTTON_WIDTH,
      CONTROL_HEIGHT,
      originalAddButtonText
    ) { addItemFromTextField() }

    newItemTextField.setResponder { updateTextFieldAndButtonState(it) }

    addControl(itemDisplayList)
    addControl(backButton)
    addControl(newItemTextField)
    addControl(addButton)

    super.initializeUi()
  }

  private fun saveAndReturn() {
    DropConfirmConfig.blacklistedItems = itemsList
    DropConfirmConfig.save()
    ClientGuiUtils.setScreen(minecraft, parentScreen)
  }

  private fun addItemFromTextField() {
    val itemIdText = newItemTextField.controlMessage.trim().takeIf { it.isNotEmpty() } ?: return

    val fullResourceName = if (':' in itemIdText) itemIdText else "minecraft:$itemIdText"
    val resourceLocation = ResourceLocation.tryParse(fullResourceName) ?: run {
      showError("Invalid ID format!")
      return
    }

    val item = Registry./*? if fabric {*/ITEM.get/*?} else {*//*ITEMS.getValue*//*?}*/(resourceLocation)

    when (item) {
      Items.AIR -> showError("Item not found!")
      in itemsList -> showError("Already in list!")
      else -> {
        //? if >=1.18.2 {
        @Suppress("SENSELESS_COMPARISON")
        if (item != null)
        //?}
          itemsList.add(item)
        newItemTextField.controlMessage = ""
        updateVisualList()
        updateTextFieldAndButtonState("")
      }
    }
  }

  private fun showError(message: String) {
    addButton.controlMessage = message
    addButton.textColor = Color.ERROR
  }

  private fun removeItemAt(index: Int) {
    if (index in itemsList.indices) {
      itemsList.removeAt(index)
      updateVisualList()
    }
  }

  private fun updateVisualList() {
    val displayNames = itemsList.map { getItemDisplayName(it) }
    itemDisplayList.setListAndUpdate(displayNames, itemsList)
  }

  private fun getItemDisplayName(item: Item) = item.getName(ItemStack(item)).string

  private fun isInputValidAndAddable(text: String): Boolean {
    val trimmedText = text.trim()

    if (trimmedText.isEmpty()) return false

    val fullResourceName = trimmedText.takeIf { it.contains(':') } ?: "minecraft:$trimmedText"

    val resourceLocation = ResourceLocation.tryParse(fullResourceName) ?: return false

    val item = Registry./*? if fabric {*/ITEM.get/*?} else {*//*ITEMS.getValue*//*?}*/(resourceLocation)

    return item != Items.AIR && !itemsList.contains(item)
  }

  private fun updateTextFieldAndButtonState(currentText: String) {
    if (addButton.controlMessage != originalAddButtonText) {
      addButton.controlMessage = originalAddButtonText
      addButton.textColor = Color.TEXT
    }

    newItemTextField.setTextColor(
      when {
        currentText.trim().isEmpty() -> Color.TEXT
        isInputValidAndAddable(currentText) -> Color.SUCCESS
        else -> Color.ERROR
      }()
    )
  }

  //? if >=1.18.2 {
  override fun narrationPriority(): NarratableEntry.NarrationPriority = NarratableEntry.NarrationPriority.NONE
  override fun updateNarration(p0: NarrationElementOutput) = Unit
  //?}

  inner class ItemList(
    mc: Minecraft,
    owningScreen: ExtendedScreen,
    width: Int,
    height: Int,
    top: Int,
    bottom: Int,
    initialItems: List<Item>
  ) : ScrollableListControl(
    mc, owningScreen, width, height, top, bottom,
    ENTRY_HEIGHT,
    initialItems.map { getItemDisplayName(it) },
    null
  ) {
    private var currentItems: List<Item> = initialItems.toList()
    private val removeButtons = mutableMapOf<Int, ButtonControl>()

    fun setListAndUpdate(displayNames: List<String>, actualItems: List<Item>) {
      setList(displayNames)
      currentItems = actualItems.toList()
      removeButtons.keys.retainAll { it < actualItems.size }
    }

    override fun renderSlotItem(
      /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
      originalName: String?,
      x: Int,
      y: Int,
      slotWidth: Int,
      slotHeight: Int,
      mouseX: Int,
      mouseY: Int,
      isHovering: Boolean,
      partialTicks: Float
    ) {
      val index = itemList.indexOf(originalName)
      if (index == -1 || index >= currentItems.size) {
        super.renderSlotItem(
          /*? if >=1.16.5 {*/poseStack,/*?}*/
          "Error: Item not found",
          x,
          y,
          slotWidth,
          slotHeight,
          mouseX,
          mouseY,
          isHovering,
          partialTicks
        )
        return
      }

      val item = currentItems[index]
      val stack = ItemStack(item)

      var currentX = x + HORIZONTAL_PADDING

      if (!ItemUtils.isItemEmpty(stack)) {
        val iconY = y + (slotHeight - ICON_SIZE) / 2
        RenderUtils.drawItemStack(
          /*? if 1.19.4 {*//*gameInstance,*//*?}*/
          /*? if >=1.19.4 {*/poseStack/*?} else {*//*gameInstance*//*?}*/,
          fontRenderer,
          currentX,
          iconY,
          stack,
          1.0f
        )
        currentX += ICON_SIZE + ICON_TEXT_PADDING
      }

      val buttonX = x + slotWidth - REMOVE_BUTTON_SIZE - BUTTON_RIGHT_MARGIN
      val buttonY = y + (slotHeight - REMOVE_BUTTON_SIZE) / 2

      val removeButton = removeButtons.getOrPut(index) {
        ButtonControl(buttonX, buttonY, REMOVE_BUTTON_SIZE, REMOVE_BUTTON_SIZE, "-") {
          this@DropConfirmListEditorScreen.removeItemAt(index)
        }
      }

      removeButton.controlPosX = buttonX
      removeButton.controlPosY = buttonY

      removeButton.isFocusedOver = removeButton.isControlVisible && removeButton.isControlEnabled &&
        RenderUtils.isMouseOver(mouseX.toDouble(), mouseY.toDouble(), removeButton)

      removeButton.render(
        /*? if >=1.16.5 {*/poseStack,/*?}*/
        mouseX,
        mouseY,
        partialTicks
      )

      val textWidth = buttonX - currentX - HORIZONTAL_PADDING
      super.renderSlotItem(
        /*? if >=1.16.5 {*/poseStack,/*?}*/
        originalName,
        currentX,
        y,
        textWidth,
        slotHeight,
        mouseX,
        mouseY,
        isHovering,
        partialTicks
      )
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
      for ((_, removeBtn) in removeButtons)
        if (removeBtn.isControlVisible && removeBtn.isMouseOver(mouseX, mouseY)) {
          removeBtn.onPress()
          return true
        }
      return super.mouseClicked(mouseX, mouseY, button)
    }

    //? if >=1.18.2 {
    override fun narrationPriority(): NarratableEntry.NarrationPriority = NarratableEntry.NarrationPriority.NONE
    override fun updateNarration(p0: NarrationElementOutput) = Unit
    //?}
  }
}
*///?}
