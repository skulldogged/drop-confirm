package dev.skulldogged.drop_confirm.util

//? if <=1.18.2 {
/*import net.minecraft.core.Registry as BuiltInRegistries
*///?} else {
import net.minecraft.core.registries.BuiltInRegistries
//?}
import net.minecraft.resources./*$ identifier_type {*/ResourceLocation/*$}*/ as ResId
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

/**
 * Version-agnostic helpers for turning item IDs into items and back.
 */
object ItemUtils {
  /**
   * Parses user input such as `stone` or `mod_id:item` into a registry ID.
   * A missing namespace defaults to `minecraft`.
   */
  @JvmStatic
  fun parseId(text: String): ResId? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null

    val full = if (':' in trimmed) trimmed else "minecraft:$trimmed"
    return ResId.tryParse(full)
  }

  /** Looks up a registered item, treating air as "not found". */
  @JvmStatic
  fun lookup(id: ResId): Item? {
    val item: Item? = BuiltInRegistries.ITEM.getOptional(id).orElse(null)

    return if (item == null || item === Items.AIR) null else item
  }

  @JvmStatic
  fun lookup(text: String): Item? = parseId(text)?.let { lookup(it) }

  @JvmStatic
  fun idOf(item: Item): String =
    BuiltInRegistries.ITEM.getKey(item).toString()

  /**
   * A stack of [item] for display, or null when stacks cannot be built yet.
   *
   * On newer versions item components are only bound once a world's registries
   * are loaded, so building a stack from the main menu throws.
   */
  @JvmStatic
  fun displayStack(item: Item): ItemStack? = runCatching { ItemStack(item) }.getOrNull()

  /** The item's translated name, falling back to its registry ID when the name is unavailable. */
  @JvmStatic
  fun displayName(item: Item): String =
    runCatching { item.getName(item.defaultInstance).string }.getOrElse { idOf(item) }
}
