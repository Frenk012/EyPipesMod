package frenk.eypipes.particle;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.registries.ModParticles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Enhanced Particle Helper - Coordinates complex particle effects for stunning visuals.
 * Manages layered particle systems and synchronized effects.
 * Creates cinematic smoke, ember, and spark displays during pipe usage.
 *
 * All methods must be called from the client render thread.
 */
public class EnhancedParticleHelper {

    /**
     * Spawn the pipe bowl ember effect - glowing particles and occasional sparks.
     * Call this during active smoking for a realistic burning tobacco effect.
     */
    public static void spawnBowlEmbers(Level level, Vec3 bowlPosition, float intensity) {
        if (!level.isClientSide()) return;

        RandomSource random = level.random;

        // === Ember particles (glowing orange) ===
        if (EyPipesConfig.CLIENT.enableEmberParticles.get()) {
            int emberCount = 1 + random.nextInt(2);
            for (int i = 0; i < emberCount; i++) {
                level.addParticle(ModParticles.EMBER.get(),
                        bowlPosition.x + (random.nextDouble() - 0.5) * 0.03,
                        bowlPosition.y,
                        bowlPosition.z + (random.nextDouble() - 0.5) * 0.03,
                        (random.nextDouble() - 0.5) * 0.01,
                        0.02 + random.nextDouble() * 0.02,
                        (random.nextDouble() - 0.5) * 0.01);
            }
        }

        // === Occasional sparks (bright and brief) ===
        if (EyPipesConfig.CLIENT.enableSparkParticles.get() && random.nextFloat() < 0.15f * intensity) {
            int sparkCount = 1 + random.nextInt(3);
            for (int i = 0; i < sparkCount; i++) {
                level.addParticle(ModParticles.SPARK.get(),
                        bowlPosition.x + (random.nextDouble() - 0.5) * 0.02,
                        bowlPosition.y,
                        bowlPosition.z + (random.nextDouble() - 0.5) * 0.02,
                        (random.nextDouble() - 0.5) * 0.05,
                        0.05 + random.nextDouble() * 0.08,
                        (random.nextDouble() - 0.5) * 0.05);
            }
        }

        // === Small smoke wisps from bowl ===
        if (random.nextFloat() < 0.3f) {
            level.addParticle(ModParticles.SMOKE_STREAM.get(),
                    bowlPosition.x + (random.nextDouble() - 0.5) * 0.02,
                    bowlPosition.y + 0.02,
                    bowlPosition.z + (random.nextDouble() - 0.5) * 0.02,
                    0, 0.01, 0);
        }
    }

    // ========================================
    // FIRST-PERSON SPECIFIC PARTICLE METHODS
    // Smaller, more discrete particles for first-person view
    // ========================================

    /**
     * Spawn smaller, discrete first-person smoke particles at the model's locator position.
     * Used during active smoking for subtle visual feedback without obstructing view.
     * Particles are 50% smaller and less frequent than third-person.
     */
    public static void spawnFirstPersonBowlSmoke(Level level, Vec3 locatorWorldPos, float intensity) {
        if (!level.isClientSide()) return;

        RandomSource random = level.random;

        // Subtle smoke wisps from bowl - 40% chance based on intensity
        if (random.nextFloat() < 0.4f * intensity) {
            // Tiny smoke puff - reduced spread (0.02 vs 0.05)
            level.addParticle(ModParticles.SMOKE_STREAM.get(),
                    locatorWorldPos.x + (random.nextDouble() - 0.5) * 0.02,
                    locatorWorldPos.y + 0.02,
                    locatorWorldPos.z + (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.005,  // Reduced velocity
                    0.01 + random.nextDouble() * 0.01,
                    (random.nextDouble() - 0.5) * 0.005);
        }

        // Very occasional smoke wisp for visual interest
        if (EyPipesConfig.CLIENT.enableSmokeWisps.get() && random.nextFloat() < 0.1f * intensity) {
            level.addParticle(ModParticles.SMOKE_STREAM.get(),
                    locatorWorldPos.x + (random.nextDouble() - 0.5) * 0.015,
                    locatorWorldPos.y + 0.01,
                    locatorWorldPos.z + (random.nextDouble() - 0.5) * 0.015,
                    (random.nextDouble() - 0.5) * 0.008,
                    0.015,
                    (random.nextDouble() - 0.5) * 0.008);
        }
    }

    /**
     * Spawn scaled-down bowl embers specifically for first-person view.
     * Uses locator position from renderer for accurate placement.
     * Embers are smaller and less frequent to avoid obstructing view.
     */
    public static void spawnFirstPersonBowlEmbers(Level level, Vec3 bowlPosition, float intensity) {
        if (!level.isClientSide()) return;

        RandomSource random = level.random;

        // Reduced ember particles - 30% base chance (vs 50%+ in third person)
        if (EyPipesConfig.CLIENT.enableEmberParticles.get() && random.nextFloat() < 0.3f) {
            // Smaller spread: 0.015 instead of 0.03
            level.addParticle(ModParticles.EMBER.get(),
                    bowlPosition.x + (random.nextDouble() - 0.5) * 0.015,
                    bowlPosition.y,
                    bowlPosition.z + (random.nextDouble() - 0.5) * 0.015,
                    (random.nextDouble() - 0.5) * 0.008,  // Reduced velocity
                    0.015 + random.nextDouble() * 0.015,
                    (random.nextDouble() - 0.5) * 0.008);
        }

        // Very occasional tiny spark - 5% chance based on intensity
        if (EyPipesConfig.CLIENT.enableSparkParticles.get() && random.nextFloat() < 0.05f * intensity) {
            level.addParticle(ModParticles.SPARK.get(),
                    bowlPosition.x + (random.nextDouble() - 0.5) * 0.01,
                    bowlPosition.y,
                    bowlPosition.z + (random.nextDouble() - 0.5) * 0.01,
                    (random.nextDouble() - 0.5) * 0.02,
                    0.03 + random.nextDouble() * 0.04,
                    (random.nextDouble() - 0.5) * 0.02);
        }
    }

}
