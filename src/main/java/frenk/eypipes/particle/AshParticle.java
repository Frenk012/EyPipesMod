package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Ash particle effect - Gray particles falling with gravity.
 * Features gravity-affected falling, horizontal drift, and fade out.
 * NEW particle type for enhanced visual effects.
 */
@OnlyIn(Dist.CLIENT)
public class AshParticle extends TextureSheetParticle {
    private final float rotationSpeed;

    public AshParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, velX, velY, velZ);

        // Gray ash color with slight variation
        float grayValue = 0.3F + this.random.nextFloat() * 0.2F;
        this.rCol = grayValue;
        this.gCol = grayValue;
        this.bCol = grayValue;

        // Small size
        this.quadSize = 0.03F + this.random.nextFloat() * 0.02F;

        // Medium lifetime
        this.lifetime = 40 + this.random.nextInt(30);

        // Downward motion with horizontal drift
        this.xd = velX;
        this.yd = velY - 0.02; // Slight downward bias
        this.zd = velZ;

        this.gravity = 0.04F; // Falls with gravity

        // Random rotation
        this.roll = this.random.nextFloat() * (float) Math.PI * 2;
        this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.1F;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isAlive()) {
            // Rotate while falling
            this.oRoll = this.roll;
            this.roll += rotationSpeed;

            // Horizontal drift (like falling leaves)
            float driftPhase = (float) this.age * 0.1F;
            this.xd += Math.sin(driftPhase) * 0.001;
            this.zd += Math.cos(driftPhase) * 0.001;

            // Fade out near end of life
            float ageRatio = (float) this.age / this.lifetime;
            if (ageRatio > 0.7F) {
                this.setAlpha(1.0F - ((ageRatio - 0.7F) / 0.3F));
            }
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
            return new AshParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
