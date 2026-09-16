package dev.skulldogged.drop_confirm.util

//? if >=1.19.4 {
import net.minecraft.network.chat.MutableComponent
//?} else {
/*import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
*///?}
import net.minecraft.network.chat.Component

//? if >=1.19.4 {
typealias TextComponent = MutableComponent
typealias TranslatableComponent = MutableComponent
//?}
/**
 * Utility class for creating text components across different Minecraft versions.
 * This provides a version-agnostic API for working with Minecraft's text component system.
 */
object ComponentUtils {
  /**
   * Creates a literal text component.
   *
   * @param text The text content
   * @return A text component containing the literal text
   */
  @JvmStatic
  fun literal(text: String): TextComponent =
    /*? if >=1.19.4 {*/Component.literal/*?} else {*//*TextComponent*//*?}*/(text)

  /**
   * Creates a translatable text component.
   *
   * @param key The translation key
   * @param args Optional formatting arguments
   * @return A text component that will be translated using the client's language settings
   */
  @JvmStatic
  fun translatable(key: String, vararg args: Any): TranslatableComponent =
    /*? if >=1.19.4 {*/Component.translatable/*?} else {*//*TranslatableComponent*//*?}*/(key, *args)

  /**
   * Creates an empty text component.
   *
   * @return An empty text component
   */
  @JvmStatic
  // @formatter:off
  fun empty(): Component =
    /*? if >=1.19.4 {*/Component.empty()/*?} else if >=1.16.5 {*//*TextComponent.EMPTY*//*?} else {*//*TextComponent("")*//*?}*/
  // @formatter:on
}
