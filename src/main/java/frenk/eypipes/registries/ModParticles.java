package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for all EyPipes particle types
 */
public class ModParticles {
    public static final Registrar<ParticleType<?>> PARTICLE_TYPES = Registrar.create(Registries.PARTICLE_TYPE);

    // Ring of Smoke particle - animated smoke ring for pipe exhale
    public static final RegistryEntry<SimpleParticleType> RING_OF_SMOKE =
            PARTICLE_TYPES.register("ring_of_smoke_particles",
                    () -> new SimpleParticleType(false));

    // NEW: Ember particle - glowing embers rising from pipe bowl
    public static final RegistryEntry<SimpleParticleType> EMBER =
            PARTICLE_TYPES.register("ember",
                    () -> new SimpleParticleType(false));


    // NEW: Spiral smoke particle - 3D helix smoke effect
    public static final RegistryEntry<SimpleParticleType> SPIRAL_SMOKE =
            PARTICLE_TYPES.register("spiral_smoke",
                    () -> new SimpleParticleType(false));

    // NEW: Spark particle - bright energetic sparks
    public static final RegistryEntry<SimpleParticleType> SPARK =
            PARTICLE_TYPES.register("spark",
                    () -> new SimpleParticleType(false));

    // NEW: Smoke stream particle - continuous flowing smoke
    public static final RegistryEntry<SimpleParticleType> SMOKE_STREAM =
            PARTICLE_TYPES.register("smoke_stream",
                    () -> new SimpleParticleType(false));

    // NEW: Steam particle - for drying rack visualization
    public static final RegistryEntry<SimpleParticleType> STEAM =
            PARTICLE_TYPES.register("steam",
                    () -> new SimpleParticleType(false));

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
        EyPipes.LOGGER.info("Registering EyPipes Particles");
    }
}
