package frenk.eypipes.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Version seam for block entity persistence.
 *
 * <p>Minecraft 1.21.6 moved block entity serialization off {@code CompoundTag} onto
 * {@code ValueInput}/{@code ValueOutput}: reads become defaulted accessors and item stacks are
 * stored through a codec instead of {@code ItemStack#save}/{@code ItemStack#parse}. EyPipes' five
 * block entities only ever do three things — store a stack under a key, read it back, and read a
 * primitive with a fallback — so they express that intent here and stay unchanged across the break.
 *
 * <p>An absent key and an empty stack are the same state: {@link #putStack} writes nothing for an
 * empty stack, and {@link #getStack} returns {@link ItemStack#EMPTY} for a missing key.
 *
 * <p>Current implementation targets Minecraft 1.21.1.
 */
public final class BlockEntityNbt {

    private BlockEntityNbt() {}

    /** Store a stack under {@code key}, writing nothing if the stack is empty. */
    public static void putStack(CompoundTag tag, String key, ItemStack stack,
            HolderLookup.Provider registries) {
        if (!stack.isEmpty()) {
            tag.put(key, stack.save(registries));
        }
    }

    /** Read the stack stored under {@code key}, or {@link ItemStack#EMPTY} if there is none. */
    public static ItemStack getStack(CompoundTag tag, String key, HolderLookup.Provider registries) {
        if (!tag.contains(key)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.parse(registries, tag.getCompound(key)).orElse(ItemStack.EMPTY);
    }

    /** Read a long, falling back to {@code fallback} when the key is absent. */
    public static long getLong(CompoundTag tag, String key, long fallback) {
        return tag.contains(key) ? tag.getLong(key) : fallback;
    }

    /** Read an int, falling back to {@code fallback} when the key is absent. */
    public static int getInt(CompoundTag tag, String key, int fallback) {
        return tag.contains(key) ? tag.getInt(key) : fallback;
    }

    /** Store a long under {@code key}. */
    public static void putLong(CompoundTag tag, String key, long value) {
        tag.putLong(key, value);
    }

    /** Store an int under {@code key}. */
    public static void putInt(CompoundTag tag, String key, int value) {
        tag.putInt(key, value);
    }
}
