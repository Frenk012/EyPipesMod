package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Spark particle effect - Bright, energetic sparks that pop and fade quickly.
 * Features rapid color shifts between orange/yellow/white, erratic motion, and bloom effect.
 * Creates a magical, fiery appearance when smoking the pipe.
 */
@OnlyIn(Dist.CLIENT)
public class SparkParticle extends TextureSheetParticle {
    private final float baseScale;
    private final float flickerRate;
    private final double initialVelY;

    // Spark colors: shifts between hot orange, bright yellow, and white
    private static final float[][] SPARK_COLORS = {
        {1.0f, 0.6f, 0.0f},   // Hot orange
        {1.0f, 0.85f, 0.2f},  // Bright yellow
        {1.0f, 1.0f, 0.8f},   // Near white
        {1.0f, 0.7f, 0.3f}    // Gold
    };

    public SparkParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, velX, velY, velZ);

        // Random initial color from palette
        int colorIndex = this.random.nextInt(SPARK_COLORS.length);
        this.rCol = SPARK_COLORS[colorIndex][0];
        this.gCol = SPARK_COLORS[colorIndex][1];
        this.bCol = SPARK_COLORS[colorIndex][2];

        // Tiny but bright
        this.baseScale = 0.015f + this.random.nextFloat() * 0.015f;
        this.quadSize = baseScale;

        // Very short lifetime - sparks are brief
        this.lifetime = 8 + this.random.nextInt(12);

        // Erratic motion - sparks fly in random directions
        this.xd = velX + (this.random.nextDouble() - 0.5) * 0.08;
        this.yd = Math.abs(velY) + 0.05 + this.random.nextDouble() * 0.08;
        this.zd = velZ + (this.random.nextDouble() - 0.5) * 0.08;
        this.initialVelY = this.yd;

        // Slight gravity to arc the sparks
        this.gravity = 0.06f;

        // Flicker rate for color/size pulsing
        this.flickerRate = 0.3f + this.random.nextFloat() * 0.4f;

        this.hasPhysics = true;

        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isAlive()) {
            // Rapid flickering effect
            float flicker = (float) Math.sin(this.age * flickerRate * Math.PI) * 0.5f + 0.5f;

            // Size pulses
            this.quadSize = baseScale * (0.5f + flicker);

            // Color shifts rapidly between hot colors
            int colorIndex = (this.age / 2) % SPARK_COLORS.length;
            int nextColorIndex = (colorIndex + 1) % SPARK_COLORS.length;
            float colorT = ((this.age % 2) + flicker) / 2f;

            this.rCol = lerp(SPARK_COLORS[colorIndex][0], SPARK_COLORS[nextColorIndex][0], colorT);
            this.gCol = lerp(SPARK_COLORS[colorIndex][1], SPARK_COLORS[nextColorIndex][1], colorT);
            this.bCol = lerp(SPARK_COLORS[colorIndex][2], SPARK_COLORS[nextColorIndex][2], colorT);

            // Brightness boost based on flicker
            float brightnessMult = 0.8f + flicker * 0.4f;
            this.rCol = Math.min(1.0f, this.rCol * brightnessMult);
            this.gCol = Math.min(1.0f, this.gCol * brightnessMult);
            this.bCol = Math.min(1.0f, this.bCol * brightnessMult);

            // Add some random jitter for erratic motion
            this.xd += (this.random.nextDouble() - 0.5) * 0.01;
            this.zd += (this.random.nextDouble() - 0.5) * 0.01;

            // Fade out in final moments
            float ageRatio = (float) this.age / this.lifetime;
            if (ageRatio > 0.5f) {
                this.setAlpha(1.0f - ((ageRatio - 0.5f) * 2f));
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * Math.max(0, Math.min(1, t));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        // Sparks are self-luminous - full brightness
        return 0xF000F0;
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
            return new SparkParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
