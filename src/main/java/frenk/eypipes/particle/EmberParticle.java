package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Ember particle effect - Small glowing orange particles rising from pipe bowl.
 * Features rising motion, brightness flickering, and short lifespan.
 * NEW particle type for enhanced visual effects.
 */
@OnlyIn(Dist.CLIENT)
public class EmberParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final float baseRed;
    private final float baseGreen;

    public EmberParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, velX, velY, velZ);
        this.spriteSet = spriteSet;

        // Ember colors (orange to red)
        this.baseRed = 1.0F;
        this.baseGreen = 0.4F + this.random.nextFloat() * 0.3F;
        this.rCol = baseRed;
        this.gCol = baseGreen;
        this.bCol = 0.0F;

        // Small size
        this.quadSize = 0.02F + this.random.nextFloat() * 0.02F;

        // Short lifetime
        this.lifetime = 20 + this.random.nextInt(20);

        // Rising motion
        this.xd = velX + (this.random.nextDouble() - 0.5) * 0.02;
        this.yd = Math.abs(velY) * 0.5 + 0.02;
        this.zd = velZ + (this.random.nextDouble() - 0.5) * 0.02;

        this.gravity = -0.01F; // Slight upward float

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isAlive()) {
            // Flicker brightness
            float flicker = 0.7F + this.random.nextFloat() * 0.3F;
            this.rCol = baseRed * flicker;
            this.gCol = baseGreen * flicker;

            // Fade out near end of life
            float ageRatio = (float) this.age / this.lifetime;
            if (ageRatio > 0.6F) {
                this.setAlpha(1.0F - ((ageRatio - 0.6F) / 0.4F));
            }

            // Slight horizontal drift
            this.xd += (this.random.nextDouble() - 0.5) * 0.005;
            this.zd += (this.random.nextDouble() - 0.5) * 0.005;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        // Embers glow brightly
        return 0xF000F0; // Full brightness
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
            return new EmberParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
