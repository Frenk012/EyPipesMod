package frenk.eypipes.client;

import frenk.eypipes.client.layer.BurningTobaccoLayer;
import frenk.eypipes.client.renderer.PipeGeoRenderer;
import frenk.eypipes.particle.EnhancedParticleHelper;
import frenk.eypipes.particle.FirstPersonSmoke;
import frenk.eypipes.registries.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The single seam between EyPipes' common item logic and client-only rendering APIs.
 *
 * <p>{@code PipeItem} and {@code CigarItem} run on both sides but need client behaviour inside
 * their {@code level.isClientSide()} branches. Routing that through this class keeps every
 * reference to {@code net.minecraft.client} and to EyPipes' own renderer/particle classes out of
 * common code, so the dedicated server never has a reason to load them — and so the client API
 * churn between Minecraft versions is confined to one file instead of spread across the item logic.
 *
 * <p>Every method here must only be called from a {@code level.isClientSide()} branch.
 */
public final class SmokeClientEffects {

    private SmokeClientEffects() {}

    /**
     * Dispatch a task onto the client render thread. Particle spawning is not thread-safe, so
     * anything scheduled off a background executor must come back through here.
     */
    public static void runOnRenderThread(Runnable task) {
        Minecraft.getInstance().execute(task);
    }

    /**
     * Whether the pipe held by this entity is currently being rendered in the local player's
     * first-person view, in which case particles come from the model's bowl locator rather than
     * from an eye-relative estimate.
     */
    public static boolean isFirstPersonPipeView(LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        return entity == minecraft.player
                && minecraft.options.getCameraType().isFirstPerson()
                && PipeGeoRenderer.isCurrentlySmokingFirstPerson();
    }

    /**
     * Spawn the small, discrete bowl particles at the renderer's locator position.
     * Does nothing if the renderer has not reported a locator position yet.
     */
    public static void spawnFirstPersonBowl(Level level, float intensity) {
        Vec3 locatorPos = PipeGeoRenderer.getLastLocatorWorldPos();
        if (locatorPos != Vec3.ZERO) {
            FirstPersonSmoke.run(() -> EnhancedParticleHelper.spawnFirstPersonBowlSmoke(level, locatorPos, intensity));
            EnhancedParticleHelper.spawnFirstPersonBowlEmbers(level, locatorPos, intensity);
        }
    }

    /**
     * Whether this entity is the local player looking through their own eyes, in which case
     * smoke is placed in front of the camera and follows it.
     */
    public static boolean isLocalFirstPerson(LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        return entity == minecraft.player && minecraft.options.getCameraType().isFirstPerson();
    }

    /**
     * Spawn the full-size smoke plume and bowl embers at the given position.
     */
    public static void spawnThirdPersonBowl(Level level, Vec3 position, float intensity) {
        for (int i = 0; i < 10; i++) {
            level.addParticle(ModParticles.SMOKE_STREAM.get(),
                    position.x + (level.random.nextGaussian() * 0.02),
                    position.y + (level.random.nextGaussian() * 0.02),
                    position.z + (level.random.nextGaussian() * 0.02),
                    0.001, 0.01, 0.001);
        }

        EnhancedParticleHelper.spawnBowlEmbers(level, position, intensity);
    }

    /**
     * Tell the burning-tobacco render layer that this pipe has been lit.
     */
    public static void onStartSmoking(Player player, ItemStack pipeStack) {
        BurningTobaccoLayer.onStartSmoking(player, pipeStack);
    }

    /**
     * Tell the burning-tobacco render layer that this pipe has been put out, so the bowl fades
     * through its afterglow instead of snapping dark.
     */
    public static void onStopSmoking(Player player, ItemStack pipeStack, long gameTime) {
        BurningTobaccoLayer.onStopSmoking(player, pipeStack, gameTime);
    }

    /**
     * Spawn a single exhaled smoke ring travelling along the entity's current view vector.
     */
    public static void spawnSmokeRing(LivingEntity entity, Level level,
            double offsetMultiplier, float velocityScale) {
        Vec3 vec = entity.getViewVector(1.0F);
        level.addParticle(ModParticles.RING_OF_SMOKE.get(),
                entity.getX() + vec.x * offsetMultiplier,
                entity.getY() + entity.getEyeHeight() + vec.y * offsetMultiplier,
                entity.getZ() + vec.z * offsetMultiplier,
                vec.x * velocityScale,
                vec.y * velocityScale,
                vec.z * velocityScale);
    }
}
