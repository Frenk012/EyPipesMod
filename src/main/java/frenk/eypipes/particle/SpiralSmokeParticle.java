package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Enchanted Spiral Smoke - Dramatic 3D helix pattern for exhale effect.
 * Features majestic spiraling motion, magical color shifts, and ethereal presence.
 * Creates stunning smoke formations that dance and swirl with mystical energy.
 */
@OnlyIn(Dist.CLIENT)
public class SpiralSmokeParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;
    private final double startX;
    private final double startY;
    private final double startZ;

    // Motion parameters
    private final float helixRadius;
    private final float helixSpeed;
    private final float riseSpeed;
    private final boolean clockwise;
    private final float waviness;

    // Visual parameters
    private final float baseAlpha;
    private final int colorStyle;
    private final float phaseOffset;

    // Majestic color palettes for exhale
    private static final float[][] SILVER_MIST = {
        {0.98f, 0.98f, 1.0f},   // Pure silver-white
        {0.92f, 0.94f, 0.98f},  // Cool silver
        {0.85f, 0.88f, 0.95f},  // Silver-blue
        {0.78f, 0.82f, 0.92f}   // Deep silver
    };

    private static final float[][] AURORA_SMOKE = {
        {0.95f, 0.98f, 0.92f},  // Pale aurora green-white
        {0.88f, 0.95f, 0.90f},  // Soft aurora
        {0.80f, 0.92f, 0.88f},  // Aurora green
        {0.72f, 0.88f, 0.85f}   // Deep aurora
    };

    private static final float[][] SUNSET_HAZE = {
        {1.0f, 0.95f, 0.88f},   // Warm sunset white
        {0.98f, 0.88f, 0.78f},  // Peach
        {0.95f, 0.80f, 0.70f},  // Sunset orange
        {0.90f, 0.72f, 0.65f}   // Deep sunset
    };

    private static final float[][] ETHEREAL_PURPLE = {
        {0.98f, 0.95f, 1.0f},   // Ethereal white
        {0.92f, 0.88f, 0.98f},  // Light purple
        {0.85f, 0.80f, 0.95f},  // Medium purple
        {0.78f, 0.72f, 0.90f}   // Deep purple
    };

    public SpiralSmokeParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.spriteSet = spriteSet;
        this.startX = x;
        this.startY = y;
        this.startZ = z;

        // Majestic helix parameters
        this.helixRadius = 0.06f + this.random.nextFloat() * 0.08f;
        this.helixSpeed = 0.12f + this.random.nextFloat() * 0.08f;
        this.riseSpeed = 0.035f + this.random.nextFloat() * 0.02f;
        this.clockwise = this.random.nextBoolean();
        this.waviness = 0.3f + this.random.nextFloat() * 0.4f;

        // Color style with weighted randomness
        float roll = this.random.nextFloat();
        if (roll < 0.45f) {
            this.colorStyle = 0; // Silver - 45%
        } else if (roll < 0.70f) {
            this.colorStyle = 1; // Aurora - 25%
        } else if (roll < 0.88f) {
            this.colorStyle = 2; // Sunset - 18%
        } else {
            this.colorStyle = 3; // Purple - 12%
        }

        // Initial color
        float[][] palette = getColorPalette();
        this.rCol = palette[0][0];
        this.gCol = palette[0][1];
        this.bCol = palette[0][2];

        // Phase offset for variety
        this.phaseOffset = this.random.nextFloat() * 6.28f;

        // High alpha for dramatic effect
        this.baseAlpha = 0.9f + this.random.nextFloat() * 0.1f;
        this.alpha = 0.0f;

        // Starting size
        this.quadSize = 0.05f + this.random.nextFloat() * 0.02f;

        // Longer lifetime for majestic trails
        this.lifetime = 90 + this.random.nextInt(50);

        // Velocity influence
        this.xd = velX;
        this.yd = velY;
        this.zd = velZ;

        this.gravity = 0;
        this.hasPhysics = false;

        this.pickSprite(spriteSet);
    }

    private float[][] getColorPalette() {
        return switch (colorStyle) {
            case 1 -> AURORA_SMOKE;
            case 2 -> SUNSET_HAZE;
            case 3 -> ETHEREAL_PURPLE;
            default -> SILVER_MIST;
        };
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
        float smoothAge = ageRatio * ageRatio * (3 - 2 * ageRatio);

        // === MAJESTIC HELIX MOTION ===
        float primaryAngle = this.age * helixSpeed * (clockwise ? 1 : -1);
        float secondaryAngle = this.age * helixSpeed * 0.6f * (clockwise ? -1 : 1);

        // Radius expands elegantly
        float expandedRadius = helixRadius * (0.6f + smoothAge * 0.6f);
        float secondaryRadius = expandedRadius * 0.35f;

        // Wavy amplitude for organic feel
        float waveAmplitude = (float) Math.sin(this.age * 0.08 + phaseOffset) * waviness * 0.02f;

        // Combined helix with wave
        double helixX = Math.cos(primaryAngle) * expandedRadius +
                       Math.sin(secondaryAngle) * secondaryRadius +
                       waveAmplitude;
        double helixZ = Math.sin(primaryAngle) * expandedRadius +
                       Math.cos(secondaryAngle) * secondaryRadius +
                       waveAmplitude;

        // Graceful rise with deceleration
        double riseAmount = this.age * riseSpeed * (1 - ageRatio * 0.35f);

        // Position with velocity influence
        this.x = this.startX + helixX + (this.xd * this.age * 0.12);
        this.y = this.startY + riseAmount + (this.yd * this.age * 0.08);
        this.z = this.startZ + helixZ + (this.zd * this.age * 0.12);

        // === MAGICAL COLOR TRANSITION ===
        float[][] palette = getColorPalette();
        int colorIndex = Math.min((int)(smoothAge * (palette.length - 1)), palette.length - 2);
        float colorT = (smoothAge * (palette.length - 1)) - colorIndex;

        float baseR = lerp(palette[colorIndex][0], palette[colorIndex + 1][0], colorT);
        float baseG = lerp(palette[colorIndex][1], palette[colorIndex + 1][1], colorT);
        float baseB = lerp(palette[colorIndex][2], palette[colorIndex + 1][2], colorT);

        // Ethereal shimmer
        float shimmer = (float) Math.sin(this.age * 0.18 + phaseOffset) * 0.06f;
        float colorPulse = (float) Math.sin(this.age * 0.1 + phaseOffset * 2) * 0.03f;

        this.rCol = Math.min(1.0f, baseR + shimmer + colorPulse);
        this.gCol = Math.min(1.0f, baseG + shimmer);
        this.bCol = Math.min(1.0f, baseB + shimmer - colorPulse * 0.5f);

        // === BREATHING ALPHA ===
        float breathPulse = (float) Math.sin(this.age * 0.12 + phaseOffset) * 0.06f;

        if (ageRatio < 0.06f) {
            this.alpha = baseAlpha * (ageRatio / 0.06f);
        } else if (ageRatio > 0.72f) {
            float fadeRatio = (ageRatio - 0.72f) / 0.28f;
            this.alpha = (baseAlpha + breathPulse) * (1 - fadeRatio * fadeRatio);
        } else {
            this.alpha = baseAlpha + breathPulse;
        }

        // === ELEGANT SIZE EVOLUTION ===
        float sizePulse = (float) Math.sin(this.age * 0.15 + phaseOffset) * 0.012f;

        if (ageRatio < 0.35f) {
            float growRatio = ageRatio / 0.35f;
            float easeGrow = growRatio * growRatio * (3 - 2 * growRatio);
            this.quadSize = 0.05f + easeGrow * 0.17f + sizePulse;
        } else if (ageRatio < 0.65f) {
            this.quadSize = 0.22f + sizePulse;
        } else {
            float shrinkRatio = (ageRatio - 0.65f) / 0.35f;
            this.quadSize = 0.22f - shrinkRatio * 0.08f + sizePulse * (1 - shrinkRatio);
        }

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
