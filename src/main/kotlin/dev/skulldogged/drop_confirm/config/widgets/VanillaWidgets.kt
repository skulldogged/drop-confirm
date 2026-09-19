package dev.skulldogged.drop_confirm.config.widgets

//? if >=1.20.4
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Checkbox
import dev.skulldogged.drop_confirm.util.ComponentUtils

/**
 * Factories for vanilla widgets, hiding the constructor and builder changes between versions.
 */
object VanillaWidgets {
  /** A widget message in the type this version's widgets expect. */
  @JvmStatic
  fun label(text: String) = /*? if >=1.16.5 {*/ComponentUtils.literal(text)/*?} else {*//*text*//*?}*/

  @JvmStatic
  fun button(x: Int, y: Int, width: Int, height: Int, text: String, onPress: (Button) -> Unit): Button =
    //? if >=1.19.4 {
    Button.builder(label(text)) { onPress(it) }.bounds(x, y, width, height).build()
    //?} else {
    /*Button(x, y, width, height, label(text)) { onPress(it) }
    *///?}

  /**
   * A vanilla checkbox. On versions with the builder API the checkbox sizes itself,
   * so [width] is ignored and [height] is only used to centre the box in the row.
   */
  @JvmStatic
  @Suppress("UNUSED_PARAMETER")
  fun checkbox(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: String,
    initial: Boolean,
    onToggle: (Boolean) -> Unit
  ): Checkbox =
    //? if >=1.20.4 {
    Checkbox.builder(label(text), Minecraft.getInstance().font)
      .pos(x, y + (height - (Minecraft.getInstance().font.lineHeight + 8)) / 2)
      .selected(initial)
      .onValueChange { _, value -> onToggle(value) }
      .build()
    //?} else {
    /*object : Checkbox(x, y, width, height, label(text), initial) {
      override fun onPress() {
        super.onPress()
        onToggle(selected())
      }
    }
    *///?}
}
