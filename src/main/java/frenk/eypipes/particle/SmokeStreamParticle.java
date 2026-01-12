package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Enchanted Smoke Stream - A magical blend of realistic and fantasy smoke.
 * Features graceful dancing motion, ethereal color shifts, and mystical glow.
 * The smoke appears alive, breathing and swirling with subtle magic.
 */
@OnlyIn(Dist.CLIENT)
public class SmokeStreamParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    // Motion parameters
    private final float spiralRadius;
    private final float spiralSpeed;
    private final float riseSpeed;
    private final boolean clockwise;
    private final float driftX;
    private final float driftZ;
    private final double startX;
    private final double startY;
    private final double startZ;

    // Visual parameters
    private final float baseAlpha;
    private final int colorStyle; // 0=warm, 1=cool, 2=golden, 3=mystical
    private final float shimmerOffset;
    private final float breathingOffset;

    // Magical color palettes
    private static final float[][] WARM_COLORS = {
        {0.95f, 0.92f, 0.88f},  // Warm white
        {0.90f, 0.85f, 0.78f},  // Cream
        {0.85f, 0.80f, 0.72f},  // Warm gray
        {0.75f, 0.72f, 0.68f}   // Soft brown-gray
    };

    private static final float[][] COOL_COLORS = {
        {0.92f, 0.94f, 0.98f},  // Cool white
        {0.85f, 0.88f, 0.95f},  // Light blue-gray
        {0.78f, 0.82f, 0.92f},  // Soft blue
        {0.70f, 0.75f, 0.88f}   // Ethereal blue
    };

    private static final float[][] GOLDEN_COLORS = {
        {1.0f, 0.98f, 0.90f},   // Bright gold-white
        {0.98f, 0.92f, 0.75f},  // Soft gold
        {0.95f, 0.85f, 0.65f},  // Warm gold
        {0.88f, 0.78f, 0.58f}   // Deep gold
    };

    private static final float[][] MYSTICAL_COLORS = {
        {0.95f, 0.90f, 0.98f},  // Lavender white
        {0.88f, 0.82f, 0.95f},  // Soft purple
        {0.82f, 0.78f, 0.92f},  // Mystical purple
        {0.75f, 0.72f, 0.88f}   // Deep mystical
    };

    public SmokeStreamParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 0, 0);
        this.spriteSet = spriteSet;
        this.startX = x;
        this.startY = y;
        this.startZ = z;

        // Randomize motion style for organic variety
        this.spiralRadius = 0.03f + this.random.nextFloat() * 0.04f;
        this.spiralSpeed = 0.08f + this.random.nextFloat() * 0.06f;
        this.riseSpeed = 0.025f + this.random.nextFloat() * 0.015f;
        this.clockwise = this.random.nextBoolean();
        this.driftX = (this.random.nextFloat() - 0.5f) * 0.008f;
        this.driftZ = (this.random.nextFloat() - 0.5f) * 0.008f;

        // Choose color style with weighted randomness (more warm/cool, less magical)
        float colorRoll = this.random.nextFloat();
        if (colorRoll < 0.40f) {
            this.colorStyle = 0; // Warm - 40%
        } else if (colorRoll < 0.75f) {
            this.colorStyle = 1; // Cool - 35%
        } else if (colorRoll < 0.90f) {
            this.colorStyle = 2; // Golden - 15%
        } else {
            this.colorStyle = 3; // Mystical - 10%
        }

        // Initial color from chosen palette
        float[][] palette = getColorPalette();
        this.rCol = palette[0][0];
        this.gCol = palette[0][1];
        this.bCol = palette[0][2];

        // Visual effect offsets for variety
        this.shimmerOffset = this.random.nextFloat() * 6.28f; // Random phase
        this.breathingOffset = this.random.nextFloat() * 6.28f;

        // Alpha with slight variation
        this.baseAlpha = 0.85f + this.random.nextFloat() * 0.15f;
        this.alpha = 0.0f; // Start invisible, fade in

        // Small starting size
        this.quadSize = 0.04f + this.random.nextFloat() * 0.02f;

        // Lifetime with variation for natural layering
        this.lifetime = 70 + this.random.nextInt(50);

        // Base velocity influence
        this.xd = velX * 0.3;
        this.yd = velY + 0.01;
        this.zd = velZ * 0.3;

        this.gravity = 0;
        this.hasPhysics = false;

        this.pickSprite(spriteSet);
    }

    private float[][] getColorPalette() {
        return switch (colorStyle) {
            case 1 -> COOL_COLORS;
            case 2 -> GOLDEN_COLORS;
            case 3 -> MYSTICAL_COLORS;
            default -> WARM_COLORS;
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
        float smoothAge = ageRatio * ageRatio * (3 - 2 * ageRatio); // Smooth step

        // === MAGICAL SPIRAL MOTION ===
        // Dual spiral for complex, organic movement
        float angle1 = this.age * spiralSpeed * (clockwise ? 1 : -1);
        float angle2 = this.age * spiralSpeed * 0.7f * (clockwise ? -1 : 1); // Counter-spiral

        // Radius grows then stabilizes
        float currentRadius = spiralRadius * (0.5f + smoothAge * 0.8f);
        float secondaryRadius = currentRadius * 0.4f;

        // Combined spiral offset
        double spiralX = Math.cos(angle1) * currentRadius + Math.sin(angle2) * secondaryRadius;
        double spiralZ = Math.sin(angle1) * currentRadius + Math.cos(angle2) * secondaryRadius;

        // Gentle drift for wind effect
        double driftOffset = this.age * 0.02;

        // Calculate new position
        double riseAmount = this.age * riseSpeed * (1 - ageRatio * 0.4f); // Slows as it ages

        this.x = this.startX + spiralX + (this.xd * this.age * 0.15) + (driftX * driftOffset);
        this.y = this.startY + riseAmount;
        this.z = this.startZ + spiralZ + (this.zd * this.age * 0.15) + (driftZ * driftOffset);

        // === ETHEREAL COLOR TRANSITION ===
        float[][] palette = getColorPalette();
        int colorIndex = Math.min((int)(smoothAge * (palette.length - 1)), palette.length - 2);
        float colorT = (smoothAge * (palette.length - 1)) - colorIndex;

        // Base color interpolation
        float baseR = lerp(palette[colorIndex][0], palette[colorIndex + 1][0], colorT);
        float baseG = lerp(palette[colorIndex][1], palette[colorIndex + 1][1], colorT);
        float baseB = lerp(palette[colorIndex][2], palette[colorIndex + 1][2], colorT);

        // Subtle shimmer effect - slight brightness pulse
        float shimmer = (float) Math.sin(this.age * 0.15 + shimmerOffset) * 0.05f;

        // Apply colors with shimmer
        this.rCol = Math.min(1.0f, baseR + shimmer);
        this.gCol = Math.min(1.0f, baseG + shimmer);
        this.bCol = Math.min(1.0f, baseB + shimmer * 0.8f);

        // === BREATHING ALPHA EFFECT ===
        // Smooth fade in, pulsing middle, gentle fade out
        float breathingPulse = (float) Math.sin(this.age * 0.1 + breathingOffset) * 0.08f;

        if (ageRatio < 0.08f) {
            // Quick fade in
            this.alpha = baseAlpha * (ageRatio / 0.08f);
        } else if (ageRatio > 0.7f) {
            // Slow fade out
            float fadeRatio = (ageRatio - 0.7f) / 0.3f;
            this.alpha = baseAlpha * (1 - fadeRatio) + breathingPulse * (1 - fadeRatio);
        } else {
            // Middle section with breathing
            this.alpha = baseAlpha + breathingPulse;
        }

        // === ORGANIC SIZE EVOLUTION ===
        // Grows gracefully, slight pulse, then gentle shrink
        float sizePulse = (float) Math.sin(this.age * 0.12 + breathingOffset) * 0.01f;

        if (ageRatio < 0.4f) {
            // Graceful growth
            float growRatio = ageRatio / 0.4f;
            this.quadSize = 0.04f + growRatio * 0.14f + sizePulse;
        } else if (ageRatio < 0.7f) {
            // Stable with pulse
            this.quadSize = 0.18f + sizePulse;
        } else {
            // Gentle shrink and dissipate
            float shrinkRatio = (ageRatio - 0.7f) / 0.3f;
            this.quadSize = 0.18f - shrinkRatio * 0.06f + sizePulse * (1 - shrinkRatio);
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
            return new SmokeStreamParticle(level, x, y, z, velX, velY, velZ, spriteSet);
        }
    }
}
