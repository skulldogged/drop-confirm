package dev.skulldogged.drop_confirm.config

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import dev.skulldogged.drop_confirm.util.ItemUtils
import dev.skulldogged.drop_confirm.util.StackUtils

/**
 * A specific item stack on the list, as opposed to an item type.
 *
 * @property id The item's registry ID, used for display when no world is loaded.
 * @property name The stack's display name when it was added.
 * @property data The encoded stack as SNBT; this is what gets compared.
 */
class ExactItem(val id: String, val name: String, val data: String) {
  val tag: CompoundTag? by lazy { StackUtils.parse(data) }

  /** True if [encoded] (from [StackUtils.encode]) describes the same item as this entry. */
  fun matches(encoded: CompoundTag): Boolean = tag == encoded

  override fun equals(other: Any?): Boolean = other is ExactItem && other.data == data
  override fun hashCode(): Int = data.hashCode()

  companion object {
    /** Captures [stack] as an entry, or null if it cannot be encoded right now. */
    @JvmStatic
    fun capture(stack: ItemStack): ExactItem? {
      val tag = StackUtils.encode(stack) ?: return null
      return ExactItem(ItemUtils.idOf(stack.item), stack.hoverName.string, tag.toString())
    }
  }
}
