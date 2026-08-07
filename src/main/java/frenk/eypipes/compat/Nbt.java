package frenk.eypipes.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
//? if <1.21.6 {
import net.minecraft.core.HolderLookup;
//?} else {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///?}

/**
 * Version seam for reading and writing saved data.
 *
 * <p>Two separate changes are absorbed here, and they do not move together:
 * <ul>
 *   <li>1.21.5 turned {@code CompoundTag}'s getters into {@code Optional}-returning ones, with
 *       {@code getIntOr}-style companions carrying the default. This affects everyone, including
 *       the items, which keep their state in a {@code CUSTOM_DATA} component and so still read a
 *       {@code CompoundTag} on every version.</li>
 *   <li>1.21.6 moved <em>block entity</em> serialization off {@code CompoundTag} onto
 *       {@code ValueInput} and {@code ValueOutput}, where stacks travel through a codec instead of
 *       {@code ItemStack#save}/{@code ItemStack#parse} and the registry lookup is implicit.</li>
 * </ul>
 *
 * <p>So the tag getters below exist on every version, while the persistence methods change their
 * carrier type at 1.21.6. The carrier is part of the enclosing method's signature, so block
 * entities still branch their save and load declarations; the mechanics below that line are shared.
 *
 * <p>An absent key and an empty stack are the same state: {@link #putStack} writes nothing for an
 * empty stack, and {@code getStack} returns {@link ItemStack#EMPTY} for a missing key.
 */
public final class Nbt {

    private Nbt() {}

    // ---- CompoundTag reads, used by the items on every version ----

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

    // ---- Block entity persistence; the carrier type changes at 1.21.6 ----

    //? if <1.21.6 {
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

    /** Read a long from saved data, falling back when the key is absent. */
    public static long getLong(CompoundTag tag, String key, long fallback) {
        //? if <1.21.5 {
        return tag.contains(key) ? tag.getLong(key) : fallback;
        //?} else
        /*return tag.getLongOr(key, fallback);*/
    }

    /** Store an int. */
    public static void putInt(CompoundTag tag, String key, int value) {
        tag.putInt(key, value);
    }

    /** Store a long. */
    public static void putLong(CompoundTag tag, String key, long value) {
        tag.putLong(key, value);
    }
    //?} else {
    /*public static void putStack(ValueOutput output, String key, ItemStack stack) {
        if (!stack.isEmpty()) {
            output.store(key, ItemStack.CODEC, stack);
        }
    }

    public static ItemStack getStack(ValueInput input, String key) {
        return input.read(key, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    public static int getInt(ValueInput input, String key, int fallback) {
        return input.getIntOr(key, fallback);
    }

    public static long getLong(ValueInput input, String key, long fallback) {
        return input.getLongOr(key, fallback);
    }

    public static void putInt(ValueOutput output, String key, int value) {
        output.putInt(key, value);
    }

    public static void putLong(ValueOutput output, String key, long value) {
        output.putLong(key, value);
    }
    *///?}
}
