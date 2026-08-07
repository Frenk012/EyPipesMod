package frenk.eypipes.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Version seam for reading and writing CompoundTag.
 *
 * <p>Minecraft 1.21.5 turned {@code CompoundTag}'s getters into {@code Optional}-returning ones
 * with {@code ...Or(key, default)} companions, and 1.21.6 moves block entity serialization off
 * {@code CompoundTag} onto {@code ValueInput}/{@code ValueOutput} entirely. EyPipes's block
 * entities only ever do three things - store a stack under a key, read it back, and read a
 * primitive with a fallback - so they express that intent here and stay unchanged across both
 * breaks.
 *
 * <p>An absent key and an empty stack are the same state: {@link #putStack} writes nothing for an
 * empty stack, and {@link #getStack} returns {@link ItemStack#EMPTY} for a missing key.
 */
public final class Nbt {

    private Nbt() {}

    /** Store a stack under {@code key}, writing nothing if the stack is empty. */
    public static void putStack(CompoundTag tag, String key, ItemStack stack,
            HolderLookup.Provider registries) {
        if (!stack.isEmpty()) {
            tag.put(key, stack.save(registries));
        }
    }

    /** Read the stack stored under {@code key}, or {@link ItemStack#EMPTY} if there is none. */
    public static ItemStack getStack(CompoundTag tag, String key, HolderLookup.Provider registries) {
        //? if <1.21.5 {
        if (!tag.contains(key)) {
            return ItemStack.EMPTY;
        }
        return ItemStack.parse(registries, tag.getCompound(key)).orElse(ItemStack.EMPTY);
        //?} else
        /*return tag.getCompound(key).flatMap(nbt -> ItemStack.parse(registries, nbt)).orElse(ItemStack.EMPTY);*/
    }

    /** Read a long, falling back to {@code fallback} when the key is absent. */
    public static long getLong(CompoundTag tag, String key, long fallback) {
        //? if <1.21.5 {
        return tag.contains(key) ? tag.getLong(key) : fallback;
        //?} else
        /*return tag.getLongOr(key, fallback);*/
    }

    /** Read a boolean, falling back to {@code fallback} when the key is absent. */
    public static boolean getBoolean(CompoundTag tag, String key, boolean fallback) {
        //? if <1.21.5 {
        return tag.contains(key) ? tag.getBoolean(key) : fallback;
        //?} else
        /*return tag.getBooleanOr(key, fallback);*/
    }

    /** Read an int, falling back to {@code fallback} when the key is absent. */
    public static int getInt(CompoundTag tag, String key, int fallback) {
        //? if <1.21.5 {
        return tag.contains(key) ? tag.getInt(key) : fallback;
        //?} else
        /*return tag.getIntOr(key, fallback);*/
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
