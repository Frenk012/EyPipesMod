package frenk.eypipes.registries;

import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import frenk.eypipes.platform.RegistryEntry;
import net.minecraft.core.component.DataComponentType;
//?} else {
/*import net.minecraft.nbt.CompoundTag;
*///?}

/**
 * One value EyPipes stores on an item stack.
 *
 * <p>From 1.20.5 it is a registered data component; before that it is a key in the stack's NBT.
 * Callers read and write through this class and never see which.
 */
public abstract class ItemValue<T> {

    public abstract T get(ItemStack stack);

    public abstract void set(ItemStack stack, T value);

    public boolean has(ItemStack stack) {
        return get(stack) != null;
    }

    public T getOrDefault(ItemStack stack, T fallback) {
        T value = get(stack);
        return value != null ? value : fallback;
    }

    //? if >=1.20.5 {
    static <T> ItemValue<T> component(RegistryEntry<DataComponentType<T>> type) {
        return new ItemValue<>() {
            @Override
            public T get(ItemStack stack) {
                return stack.get(type.get());
            }

            @Override
            public void set(ItemStack stack, T value) {
                stack.set(type.get(), value);
            }
        };
    }
    //?} else {
    /*static ItemValue<Integer> intTag(String key) {
        return new ItemValue<>() {
            @Override
            public Integer get(ItemStack stack) {
                CompoundTag tag = stack.getTag();
                return tag != null && tag.contains(key) ? tag.getInt(key) : null;
            }

            @Override
            public void set(ItemStack stack, Integer value) {
                stack.getOrCreateTag().putInt(key, value);
            }
        };
    }

    static ItemValue<Long> longTag(String key) {
        return new ItemValue<>() {
            @Override
            public Long get(ItemStack stack) {
                CompoundTag tag = stack.getTag();
                return tag != null && tag.contains(key) ? tag.getLong(key) : null;
            }

            @Override
            public void set(ItemStack stack, Long value) {
                stack.getOrCreateTag().putLong(key, value);
            }
        };
    }
    *///?}
}
