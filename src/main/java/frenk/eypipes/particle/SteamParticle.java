package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Steam particle for drying rack visualization.
 * Creates gentle white/gray particles that rise slowly and fade out.
 * Spawned during the drying process to show that herbs are being dried.
 */
@OnlyIn(Dist.CLIENT)
public class SteamParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float baseAlpha;

    public SteamParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.spriteSet = spriteSet;

        // White/light gray color
        float grayValue = 0.9F + this.random.nextFloat() * 0.1F;
        this.rCol = grayValue;
        this.gCol = grayValue;
        this.bCol = grayValue;

        // Start with low alpha (subtle effect)
        this.baseAlpha = 0.3f + this.random.nextFloat() * 0.2f;
        this.alpha = 0.0f;

        // Small size
        this.quadSize = 0.08F + this.random.nextFloat() * 0.04F;

        // Short to medium lifetime
        this.lifetime = 30 + this.random.nextInt(20);

        // Slow upward velocity with slight horizontal drift
        this.xd = (this.random.nextFloat() - 0.5) * 0.01;
        this.yd = 0.02 + this.random.nextFloat() * 0.01;
        this.zd = (this.random.nextFloat() - 0.5) * 0.01;

        this.gravity = 0;
        this.hasPhysics = false;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float ageRatio = (float) this.age / this.lifetime;

        // Move upward
        this.move(this.xd, this.yd, this.zd);

        // Slow down horizontal movement over time
        this.xd *= 0.98;
        this.zd *= 0.98;

        // Fade in quickly, then fade out slowly
        if (ageRatio < 0.1f) {
            this.alpha = baseAlpha * (ageRatio / 0.1f);
        } else if (ageRatio > 0.6f) {
            float fadeRatio = (ageRatio - 0.6f) / 0.4f;
            this.alpha = baseAlpha * (1 - fadeRatio);
        } else {
            this.alpha = baseAlpha;
        }

        // Slight size increase as it rises
        this.quadSize = (0.08F + this.random.nextFloat() * 0.04F) * (1 + ageRatio * 0.3f);

        // Update sprite for animation
        if (spriteSet != null) {
            this.setSpriteFromAge(spriteSet);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                double x, double y, double z, double velX, double velY, double velZ) {
            return new SteamParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
