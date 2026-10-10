package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Base provider for every EyPipes particle.
 *
 * <p>{@code ParticleProvider#createParticle} gained a {@link net.minecraft.util.RandomSource}
 * parameter in 1.21.9. None of this mod's particles need it - they read {@code level.random}
 * where they want randomness - so the extra parameter is swallowed here and each particle only
 * implements {@link #create}.
 */
public abstract class EyPipesParticleProvider implements ParticleProvider<SimpleParticleType> {

    protected final SpriteSet spriteSet;

    protected EyPipesParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    /** Build the particle. Called from whichever {@code createParticle} the game version has. */
    protected abstract Particle create(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ);

    //? if <1.21.9 {
    @Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
            double x, double y, double z, double velX, double velY, double velZ) {
        return create(level, x, y, z, velX, velY, velZ);
    }
    //?} else {
    /*@Override
    public Particle createParticle(SimpleParticleType type, ClientLevel level,
            double x, double y, double z, double velX, double velY, double velZ,
            net.minecraft.util.RandomSource random) {
        return create(level, x, y, z, velX, velY, velZ);
    }
    *///?}
}
