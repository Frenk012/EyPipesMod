package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for all EyPipes particle types using NeoForge DeferredRegister
 */
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, EyPipes.MOD_ID);

    // Ring of Smoke particle - animated smoke ring for pipe exhale
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RING_OF_SMOKE =
            PARTICLE_TYPES.register("ring_of_smoke_particles",
                    () -> new SimpleParticleType(false));

    // NEW: Ember particle - glowing embers rising from pipe bowl
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> EMBER =
            PARTICLE_TYPES.register("ember",
                    () -> new SimpleParticleType(false));

    // NEW: Ash particle - falling ash particles
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ASH =
            PARTICLE_TYPES.register("ash",
                    () -> new SimpleParticleType(false));

    // NEW: Smoke wisp particle - wispy smoke trails
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMOKE_WISP =
            PARTICLE_TYPES.register("smoke_wisp",
                    () -> new SimpleParticleType(false));

    // NEW: Spiral smoke particle - 3D helix smoke effect
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPIRAL_SMOKE =
            PARTICLE_TYPES.register("spiral_smoke",
                    () -> new SimpleParticleType(false));

    // NEW: Spark particle - bright energetic sparks
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SPARK =
            PARTICLE_TYPES.register("spark",
                    () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Particles");
    }
}
