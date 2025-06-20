package frenk.eypipes.sound;

import frenk.eypipes.EyPipes;
import net.minecraft.util.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class EyPipesSound {
    public static SoundEvent PIPE_EXHALE;
    public static SoundEvent PIPE_REFILL;
    public static SoundEvent PIPE_IGNITE;

    private static SoundEvent registerSoundEvent(String name) {
        Identifier identifier = new Identifier(EyPipes.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }

    public static void registerModSounds() {
        EyPipes.LOGGER.debug("Registering Mod SoundEvents for " + EyPipes.MOD_ID);
        PIPE_EXHALE = registerSoundEvent("pipe_exhale");
        PIPE_REFILL = registerSoundEvent("pipe_refill");
        PIPE_IGNITE = registerSoundEvent("pipe_ignite");
    }
}
