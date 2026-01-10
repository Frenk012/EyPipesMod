package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Smoke wisp particle effect - Thin curling smoke trails.
 * Features sine wave curling motion, size expansion, and gradual fade.
 * NEW particle type for enhanced visual effects.
 */
@OnlyIn(Dist.CLIENT)
public class SmokeWispParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float initialX;
    private final float initialZ;
    private final float curlAmplitude;
    private final float curlFrequency;

    public SmokeWispParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.spriteSet = spriteSet;

        // Light gray smoke color
        float grayValue = 0.6F + this.random.nextFloat() * 0.2F;
        this.rCol = grayValue;
        this.gCol = grayValue;
        this.bCol = grayValue;
        this.setAlpha(0.6F);

        // Start small, will expand
        this.quadSize = 0.05F;

        // Long lifetime for trailing effect
        this.lifetime = 60 + this.random.nextInt(40);

        // Base velocity
        this.xd = velX;
        this.yd = velY + 0.01; // Slight upward bias
        this.zd = velZ;

        this.gravity = -0.005F; // Very slight upward float

        // Store initial position for curl calculation
        this.initialX = (float) x;
        this.initialZ = (float) z;

        // Curl parameters
        this.curlAmplitude = 0.02F + this.random.nextFloat() * 0.02F;
        this.curlFrequency = 0.1F + this.random.nextFloat() * 0.1F;

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

        // Curl motion (sine wave)
        float curlPhase = this.age * curlFrequency;
        float curlOffsetX = (float) Math.sin(curlPhase) * curlAmplitude;
        float curlOffsetZ = (float) Math.cos(curlPhase) * curlAmplitude;

        // Apply movement
        this.yd -= 0.001 * this.gravity; // Apply gravity
        this.move(this.xd + curlOffsetX, this.yd, this.zd + curlOffsetZ);

        // Slow down over time
        this.xd *= 0.98;
        this.yd *= 0.98;
        this.zd *= 0.98;

        // Expand size over time
        float ageRatio = (float) this.age / this.lifetime;
        this.quadSize = 0.05F + ageRatio * 0.15F;

        // Fade out gradually
        this.setAlpha(0.6F * (1.0F - ageRatio));

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
            return new SmokeWispParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
