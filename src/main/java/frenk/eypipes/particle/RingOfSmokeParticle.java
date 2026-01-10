package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Represents a custom particle effect that simulates a ring of smoke.
 * This particle has velocity-based lifetime, gradual scaling, and fading.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
@OnlyIn(Dist.CLIENT)
public class RingOfSmokeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final double strength;
    private float scaleMultiplier = 1.0F;

    public RingOfSmokeParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.spriteSet = spriteSet;
        this.strength = Math.abs(velX) + Math.abs(velY) + Math.abs(velZ);

        this.xd = velX;
        this.yd = velY;
        this.zd = velZ;

        if (spriteSet != null) {
            this.setSpriteFromAge(spriteSet);
        }

        this.lifetime = (int) (600 * this.strength);
        this.gravity = 0;
        this.hasPhysics = true;
    }

    public RingOfSmokeParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet, float scaleMultiplier) {
        this(level, x, y, z, velX, velY, velZ, spriteSet);
        this.scaleMultiplier = scaleMultiplier;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.setSpriteFromAge(spriteSet);
            float ageRatio = (float) this.age / this.lifetime;
            this.setAlpha(1.0F - ageRatio);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        this.quadSize = ((float) this.age / this.lifetime + 0.3F) * scaleMultiplier;
        return Math.min(this.quadSize, 0.8F * scaleMultiplier);
    }

    public void setScaleMultiplier(float multiplier) {
        this.scaleMultiplier = multiplier;
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
            return new RingOfSmokeParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
