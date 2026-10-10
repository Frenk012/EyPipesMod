package frenk.eypipes.platform;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * A registered object, readable once its registry has been filled.
 *
 * <p>This is what the {@code Mod*} registries hand out on every loader, so common code never
 * names NeoForge's {@code DeferredHolder}, Forge's {@code RegistryObject} or Fabric's direct
 * registration.
 */
public final class RegistryEntry<T> implements Supplier<T> {
    private final ResourceLocation id;
    private final Supplier<? extends T> value;

    RegistryEntry(ResourceLocation id, Supplier<? extends T> value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public T get() {
        return value.get();
    }

    public ResourceLocation getId() {
        return id;
    }
}
