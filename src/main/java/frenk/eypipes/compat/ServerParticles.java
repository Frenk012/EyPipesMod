package frenk.eypipes.compat;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Version seam for server-driven particles.
 *
 * <p>The per-player {@code ServerLevel#sendParticles} overload gained an {@code alwaysShow}
 * flag in 1.21.5, sitting between {@code overrideLimiter} and the position. Both smoking items
 * send their third-person particles this way, so the extra argument is absorbed here.
 */
public final class ServerParticles {

    private ServerParticles() {}

    /** Send {@code count} particles to a single player, subject to their particle settings. */
    public static <T extends ParticleOptions> void sendTo(ServerLevel level, ServerPlayer player, T particle,
            double x, double y, double z, int count, double dx, double dy, double dz, double speed) {
        //? if <1.21.5 {
        level.sendParticles(player, particle, false, x, y, z, count, dx, dy, dz, speed);
        //?} else
        /*level.sendParticles(player, particle, false, false, x, y, z, count, dx, dy, dz, speed);*/
    }
}
