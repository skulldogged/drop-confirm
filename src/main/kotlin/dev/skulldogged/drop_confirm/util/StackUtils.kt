package dev.skulldogged.drop_confirm.util

//? if >=1.20.6 {
import net.minecraft.client.Minecraft
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
//?}
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.TagParser
import net.minecraft.world.item.ItemStack

/**
 * Serialises item stacks so that a specific item (with its name, enchantments and
 * any other data) can be stored in the config and recognised again later.
 */
object StackUtils {
  /**
   * Encodes a single-count copy of [stack], so identical items compare equal regardless
   * of stack size. Returns null for empty stacks, or when no world is loaded on versions
   * whose encoding needs registry access.
   */
  @JvmStatic
  fun encode(stack: ItemStack): CompoundTag? {
    if (stack.isEmpty) return null

    val single = stack.copy().apply { count = 1 }

    //? if >=1.20.6 {
    val registries = Minecraft.getInstance().level?.registryAccess() ?: return null
    val ops = RegistryOps.create(NbtOps.INSTANCE, registries)
    return ItemStack.CODEC.encodeStart(ops, single).result().orElse(null) as? CompoundTag
    //?} else {
    /*return single.save(CompoundTag())
    *///?}
  }

  /** Parses SNBT written by [CompoundTag.toString], or null if it is malformed. */
  @JvmStatic
  fun parse(snbt: String): CompoundTag? = runCatching {
    TagParser./*? if >=1.21.5 {*//*parseCompoundFully*//*?} else {*/parseTag/*?}*/(snbt)
  }.getOrNull()
}
