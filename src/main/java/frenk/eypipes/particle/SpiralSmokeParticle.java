package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Spiral Smoke particle effect - Creates a mesmerizing 3D helix smoke pattern.
 * Features double helix motion, color gradients, and dynamic size changes.
 * Creates a visually stunning spiraling smoke effect when exhaling from the pipe.
 */
@OnlyIn(Dist.CLIENT)
public class SpiralSmokeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final double startX;
    private final double startY;
    private final double startZ;
    private final float helixRadius;
    private final float helixSpeed;
    private final float riseSpeed;
    private final boolean clockwise;
    private final float baseAlpha;

    // Color transition: warm gray -> cool gray -> light blue tint
    private static final float[][] COLOR_GRADIENT = {
        {0.75f, 0.72f, 0.70f},  // Warm gray (start)
        {0.70f, 0.70f, 0.72f},  // Neutral gray
        {0.65f, 0.68f, 0.75f},  // Cool gray with blue tint
        {0.60f, 0.65f, 0.80f}   // Light blue-gray (end)
    };

    public SpiralSmokeParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.spriteSet = spriteSet;
        this.startX = x;
        this.startY = y;
        this.startZ = z;

        // Helix parameters with randomization for variety
        this.helixRadius = 0.08f + this.random.nextFloat() * 0.06f;
        this.helixSpeed = 0.15f + this.random.nextFloat() * 0.1f;
        this.riseSpeed = 0.03f + this.random.nextFloat() * 0.02f;
        this.clockwise = this.random.nextBoolean();

        // Initial color (warm gray)
        this.rCol = COLOR_GRADIENT[0][0];
        this.gCol = COLOR_GRADIENT[0][1];
        this.bCol = COLOR_GRADIENT[0][2];

        // Start nearly invisible, fade in then out
        this.baseAlpha = 0.7f + this.random.nextFloat() * 0.2f;
        this.alpha = 0.0f;

        // Small starting size, will grow
        this.quadSize = 0.04f;

        // Long lifetime for trailing effect
        this.lifetime = 80 + this.random.nextInt(40);

        // Store base velocity for direction reference
        this.xd = velX;
        this.yd = velY;
        this.zd = velZ;

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

        // Calculate helix position
        float angle = this.age * helixSpeed * (clockwise ? 1 : -1);
        float currentRadius = helixRadius * (1 + ageRatio * 0.5f); // Radius expands over time

        // Helix offset
        double helixX = Math.cos(angle) * currentRadius;
        double helixZ = Math.sin(angle) * currentRadius;

        // Calculate rise with slight slowdown over time
        double currentRise = this.startY + (this.age * riseSpeed * (1 - ageRatio * 0.3f));

        // Apply helix motion with base velocity influence
        double targetX = this.startX + helixX + (this.xd * this.age * 0.1);
        double targetZ = this.startZ + helixZ + (this.zd * this.age * 0.1);

        this.x = targetX;
        this.y = currentRise;
        this.z = targetZ;

        // Color transition through gradient
        int gradientIndex = Math.min((int)(ageRatio * (COLOR_GRADIENT.length - 1)), COLOR_GRADIENT.length - 2);
        float gradientT = (ageRatio * (COLOR_GRADIENT.length - 1)) - gradientIndex;

        this.rCol = lerp(COLOR_GRADIENT[gradientIndex][0], COLOR_GRADIENT[gradientIndex + 1][0], gradientT);
        this.gCol = lerp(COLOR_GRADIENT[gradientIndex][1], COLOR_GRADIENT[gradientIndex + 1][1], gradientT);
        this.bCol = lerp(COLOR_GRADIENT[gradientIndex][2], COLOR_GRADIENT[gradientIndex + 1][2], gradientT);

        // Alpha: fade in quickly, stay visible, fade out slowly
        if (ageRatio < 0.1f) {
            this.alpha = baseAlpha * (ageRatio / 0.1f); // Fade in
        } else if (ageRatio > 0.6f) {
            this.alpha = baseAlpha * (1 - (ageRatio - 0.6f) / 0.4f); // Fade out
        } else {
            this.alpha = baseAlpha;
        }

        // Size grows then shrinks slightly
        if (ageRatio < 0.5f) {
            this.quadSize = 0.04f + ageRatio * 0.24f; // Grow to 0.16
        } else {
            this.quadSize = 0.16f - (ageRatio - 0.5f) * 0.08f; // Shrink to 0.12
        }

        // Update sprite for animation
        if (spriteSet != null) {
            this.setSpriteFromAge(spriteSet);
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
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
            return new SpiralSmokeParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
