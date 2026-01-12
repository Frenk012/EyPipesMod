package frenk.eypipes.particle;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.registries.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced Particle Helper - Coordinates complex particle effects for stunning visuals.
 * Manages layered particle systems and synchronized effects.
 * Creates cinematic smoke, ember, and spark displays during pipe usage.
 *
 * Uses ScheduledExecutorService for delayed smoke ring emission at current player position.
 */
@OnlyIn(Dist.CLIENT)
public class EnhancedParticleHelper {

    // Delay between smoke rings in milliseconds
    private static final long SMOKE_RING_DELAY_MS = 600L;

    // Scheduled executor for delayed particle spawning
    private static final ScheduledExecutorService PARTICLE_EXECUTOR = Executors.newScheduledThreadPool(2);

    /**
     * Spawn the enhanced exhale effect - a spectacular display of spiraling smoke,
     * regular smoke rings, wisps, and ambient particles.
     * Smoke rings are emitted sequentially at the player's CURRENT position.
     */
    public static void spawnEnhancedExhale(LivingEntity entity, Level level, Vec3 direction, float intensity) {
        if (!level.isClientSide()) return;

        RandomSource random = level.random;
        // Eye position for immediate particles (spiral smoke, wisps, ambient)
        Vec3 eyePos = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());

        // === LAYER 1: Core Smoke Rings (emitted sequentially at current position) ===
        if (EyPipesConfig.CLIENT.enableSmokeRings.get()) {
            for (int i = 0; i < 3; i++) {
                final int particleIndex = i;
                final double offsetMultiplier = 0.3 + particleIndex * 0.05;
                final float velocityScale = intensity * 0.9f;

                // Schedule each ring with delay - position calculated when spawning
                PARTICLE_EXECUTOR.schedule(() -> {
                    if (entity.isAlive()) {
                        // Get CURRENT position and direction when spawning
                        Vec3 vec = entity.getViewVector(1.0F);
                        Vec3 currentEyePos = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
                        Vec3 offset = vec.scale(offsetMultiplier);
                        Vec3 pos = currentEyePos.add(offset);

                        level.addParticle(ModParticles.RING_OF_SMOKE.get(),
                                pos.x, pos.y, pos.z,
                                vec.x * velocityScale,
                                vec.y * velocityScale,
                                vec.z * velocityScale);
                    }
                }, particleIndex * SMOKE_RING_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        }

        // === LAYER 2: Spiral Smoke (3D helix effect) ===
        if (EyPipesConfig.CLIENT.enableSpiralSmoke.get()) {
            int spiralCount = EyPipesConfig.CLIENT.spiralSmokeCount.get();
            for (int i = 0; i < spiralCount; i++) {
                double offsetScale = 0.25 + random.nextFloat() * 0.2;
                Vec3 offset = direction.scale(offsetScale);
                Vec3 pos = eyePos.add(offset);

                // Randomization for natural look
                double spreadX = (random.nextDouble() - 0.5) * 0.1;
                double spreadZ = (random.nextDouble() - 0.5) * 0.1;

                // Vary velocity for each particle
                float velScale = 0.2f + random.nextFloat() * 0.2f;

                level.addParticle(ModParticles.SPIRAL_SMOKE.get(),
                        pos.x + spreadX, pos.y, pos.z + spreadZ,
                        direction.x * intensity * velScale,
                        direction.y * intensity * velScale + 0.02,
                        direction.z * intensity * velScale);
            }
        }

        // === LAYER 3: Smoke Wisps (Curling trails) ===
        if (EyPipesConfig.CLIENT.enableSmokeWisps.get()) {
            int wispCount = 4 + random.nextInt(3);
            for (int i = 0; i < wispCount; i++) {
                double offsetScale = 0.3 + random.nextFloat() * 0.2;
                Vec3 offset = direction.scale(offsetScale);
                Vec3 pos = eyePos.add(offset);

                level.addParticle(ModParticles.SMOKE_STREAM.get(),
                        pos.x + (random.nextDouble() - 0.5) * 0.05,
                        pos.y,
                        pos.z + (random.nextDouble() - 0.5) * 0.05,
                        direction.x * intensity * 0.4 + (random.nextDouble() - 0.5) * 0.02,
                        direction.y * intensity * 0.4 + 0.015,
                        direction.z * intensity * 0.4 + (random.nextDouble() - 0.5) * 0.02);
            }
        }

        // === LAYER 4: Ambient custom smoke for density ===
        for (int i = 0; i < 8; i++) {
            double offsetScale = 0.25 + random.nextFloat() * 0.25;
            Vec3 offset = direction.scale(offsetScale);
            Vec3 pos = eyePos.add(offset);

            level.addParticle(ModParticles.SPIRAL_SMOKE.get(),
                    pos.x + (random.nextDouble() - 0.5) * 0.1,
                    pos.y + (random.nextDouble() - 0.5) * 0.05,
                    pos.z + (random.nextDouble() - 0.5) * 0.1,
                    direction.x * intensity * 0.1,
                    direction.y * intensity * 0.1 + 0.01,
                    direction.z * intensity * 0.1);
        }
    }

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

    /**
     * Spawn the ash effect when finishing smoking.
     * Uses vanilla smoke particles for falling ash effect.
     */
    public static void spawnAshEffect(Level level, Vec3 position) {
        if (!level.isClientSide()) return;
        if (!EyPipesConfig.CLIENT.enableAshParticles.get()) return;

        RandomSource random = level.random;
        int ashCount = 3 + random.nextInt(4);

        // Use vanilla smoke for ash-like effect (falling, fading particles)
        for (int i = 0; i < ashCount; i++) {
            level.addParticle(ParticleTypes.SMOKE,
                    position.x + (random.nextDouble() - 0.5) * 0.1,
                    position.y,
                    position.z + (random.nextDouble() - 0.5) * 0.1,
                    (random.nextDouble() - 0.5) * 0.02,
                    -0.01 - random.nextDouble() * 0.02,
                    (random.nextDouble() - 0.5) * 0.02);
        }

        // Add a few final embers dying out
        for (int i = 0; i < 2; i++) {
            level.addParticle(ModParticles.EMBER.get(),
                    position.x + (random.nextDouble() - 0.5) * 0.05,
                    position.y,
                    position.z + (random.nextDouble() - 0.5) * 0.05,
                    (random.nextDouble() - 0.5) * 0.02,
                    0.01,
                    (random.nextDouble() - 0.5) * 0.02);
        }
    }

    /**
     * Spawn a dramatic ignition effect when lighting the pipe.
     * Creates a burst of sparks and embers.
     */
    public static void spawnIgnitionEffect(Level level, Vec3 position) {
        if (!level.isClientSide()) return;

        RandomSource random = level.random;

        // Burst of sparks
        if (EyPipesConfig.CLIENT.enableSparkParticles.get()) {
            for (int i = 0; i < 8; i++) {
                level.addParticle(ModParticles.SPARK.get(),
                        position.x + (random.nextDouble() - 0.5) * 0.03,
                        position.y,
                        position.z + (random.nextDouble() - 0.5) * 0.03,
                        (random.nextDouble() - 0.5) * 0.1,
                        0.1 + random.nextDouble() * 0.15,
                        (random.nextDouble() - 0.5) * 0.1);
            }
        }

        // Initial embers
        for (int i = 0; i < 5; i++) {
            level.addParticle(ModParticles.EMBER.get(),
                    position.x + (random.nextDouble() - 0.5) * 0.04,
                    position.y,
                    position.z + (random.nextDouble() - 0.5) * 0.04,
                    (random.nextDouble() - 0.5) * 0.03,
                    0.03 + random.nextDouble() * 0.04,
                    (random.nextDouble() - 0.5) * 0.03);
        }

        // Small flame-like particles
        for (int i = 0; i < 3; i++) {
            level.addParticle(ParticleTypes.FLAME,
                    position.x + (random.nextDouble() - 0.5) * 0.02,
                    position.y,
                    position.z + (random.nextDouble() - 0.5) * 0.02,
                    0, 0.02, 0);
        }
    }
}
