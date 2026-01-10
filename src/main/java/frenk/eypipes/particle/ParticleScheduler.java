package frenk.eypipes.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread-safe particle scheduler that uses the client tick system.
 * Allows scheduling particles to spawn at future ticks without threading issues.
 */
@OnlyIn(Dist.CLIENT)
public class ParticleScheduler {

    private static final Queue<ScheduledParticle> PARTICLE_QUEUE = new ConcurrentLinkedQueue<>();
    private static long currentTick = 0;

    /**
     * Schedule a particle to spawn after a delay.
     * @param particle The particle type to spawn
     * @param pos The position to spawn at
     * @param velocity The velocity of the particle
     * @param delayTicks Number of ticks to wait before spawning
     */
    public static void schedule(ParticleOptions particle, Vec3 pos, Vec3 velocity, int delayTicks) {
        long spawnTick = currentTick + delayTicks;
        PARTICLE_QUEUE.add(new ScheduledParticle(particle, pos, velocity, spawnTick));
    }

    /**
     * Called every client tick to process scheduled particles.
     * Must be called from the client tick event handler.
     */
    public static void tick() {
        currentTick++;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            // Clear queue if no level
            PARTICLE_QUEUE.clear();
            return;
        }

        // Process all particles that are ready to spawn
        ScheduledParticle particle;
        while ((particle = PARTICLE_QUEUE.peek()) != null && particle.spawnTick <= currentTick) {
            PARTICLE_QUEUE.poll();

            mc.level.addParticle(
                    particle.particle,
                    particle.pos.x, particle.pos.y, particle.pos.z,
                    particle.velocity.x, particle.velocity.y, particle.velocity.z
            );
        }
    }

    /**
     * Clear all scheduled particles.
     */
    public static void clear() {
        PARTICLE_QUEUE.clear();
    }

    private record ScheduledParticle(ParticleOptions particle, Vec3 pos, Vec3 velocity, long spawnTick) {
    }
}
