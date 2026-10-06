package frenk.eypipes.platform;

import frenk.eypipes.EyPipes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

//? if neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
//?} elif forge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?} else {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?}

/**
 * Registers EyPipes' objects into one Minecraft registry on whichever loader is running.
 *
 * <p>NeoForge and Forge collect the entries in a {@code DeferredRegister} and fill the registry
 * when the mod bus fires; Fabric registers each entry as soon as it is declared, which is why the
 * {@code Mod*} classes must be initialised from the mod initializer in dependency order
 * (blocks before items).
 */
public final class Registrar<T> {
    private static final List<Registrar<?>> ALL = new ArrayList<>();

    //? if neoforge || forge {
    private final DeferredRegister<T> deferred;
    //?} else
    /*private final ResourceKey<? extends Registry<T>> key;*/

    private Registrar(ResourceKey<? extends Registry<T>> key) {
        //? if neoforge || forge {
        this.deferred = DeferredRegister.create(key, EyPipes.MOD_ID);
        //?} else
        /*this.key = key;*/
    }

    public static <T> Registrar<T> create(ResourceKey<? extends Registry<T>> key) {
        Registrar<T> registrar = new Registrar<>(key);
        ALL.add(registrar);
        return registrar;
    }

    public <I extends T> RegistryEntry<I> register(String name, Supplier<I> factory) {
        ResourceLocation id = EyPipes.id(name);
        //? if neoforge || forge {
        return new RegistryEntry<>(id, deferred.register(name, factory));
        //?} else {
        /*@SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());
        I value = Registry.register(registry, id, factory.get());
        return new RegistryEntry<>(id, () -> value);
        *///?}
    }

    //? if neoforge || forge {
    /**
     * Hands every registrar created so far to the mod bus, which fills the registries at the
     * right time. The {@code Mod*} classes must already be loaded.
     */
    public static void registerAll(IEventBus modBus) {
        for (Registrar<?> registrar : ALL) {
            registrar.deferred.register(modBus);
        }
    }
    //?}
}
