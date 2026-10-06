package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for all EyPipes sound events
 */
public class ModSounds {
    public static final Registrar<SoundEvent> SOUND_EVENTS = Registrar.create(Registries.SOUND_EVENT);

    // Pipe exhale sound - played when finishing smoking
    public static final RegistryEntry<SoundEvent> PIPE_EXHALE =
            registerSound("pipe_exhale");

    // Pipe refill sound - played when refilling pipe with erbapipa
    public static final RegistryEntry<SoundEvent> PIPE_REFILL =
            registerSound("pipe_refill");

    // Pipe ignite sound - played when starting to smoke
    public static final RegistryEntry<SoundEvent> PIPE_IGNITE =
            registerSound("pipe_ignite");

    // Tobacco crackle sound - played periodically while smoking
    public static final RegistryEntry<SoundEvent> TOBACCO_CRACKLE =
            registerSound("tobacco_crackle");

    // Knife cutting sound - played when cutting herbs on the cutting board
    public static final RegistryEntry<SoundEvent> KNIFE_CUT =
            registerSound("knife_cut");

    private static RegistryEntry<SoundEvent> registerSound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
        EyPipes.LOGGER.info("Registering EyPipes Sounds");
    }
}
