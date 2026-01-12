package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes sound events using NeoForge DeferredRegister
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, EyPipes.MOD_ID);

    // Pipe exhale sound - played when finishing smoking
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_EXHALE =
            registerSound("pipe_exhale");

    // Pipe refill sound - played when refilling pipe with erbapipa
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_REFILL =
            registerSound("pipe_refill");

    // Pipe ignite sound - played when starting to smoke
    public static final DeferredHolder<SoundEvent, SoundEvent> PIPE_IGNITE =
            registerSound("pipe_ignite");

    // Tobacco crackle sound - played periodically while smoking
    public static final DeferredHolder<SoundEvent, SoundEvent> TOBACCO_CRACKLE =
            registerSound("tobacco_crackle");

    // Knife cutting sound - played when cutting herbs on the cutting board
    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_CUT =
            registerSound("knife_cut");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Sounds");
    }
}
