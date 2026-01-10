package frenk.eypipes.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * EyPipes Configuration using NeoForge ConfigSpec.
 * Provides in-game GUI and type-safe configuration.
 * Ported from Fabric 1.19.2 JSON config to NeoForge 1.21.1 ConfigSpec.
 */
public class EyPipesConfig {

    // Config specs
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final Common COMMON;
    public static final Client CLIENT;

    static {
        Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();

        Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }

    /**
     * Common configuration - Server-side settings
     */
    public static class Common {
        // Crop Settings
        public final ModConfigSpec.DoubleValue growthChance;
        public final ModConfigSpec.IntValue maxAge;
        public final ModConfigSpec.IntValue growUpperAge;
        public final ModConfigSpec.BooleanValue requiresSupport;

        // Drying Rack Settings
        public final ModConfigSpec.IntValue dryingTimeTicks;
        public final ModConfigSpec.IntValue maxItems;
        public final ModConfigSpec.BooleanValue requiresSunlight;

        // Loot Settings
        public final ModConfigSpec.DoubleValue erbapipaDropRolls;
        public final ModConfigSpec.DoubleValue seedsBonusRolls;
        public final ModConfigSpec.DoubleValue seedsFortuneProbability;
        public final ModConfigSpec.BooleanValue fortuneAffectsDrops;
        public final ModConfigSpec.DoubleValue erbapipaDropPercentage;
        public final ModConfigSpec.DoubleValue seedsDropPercentage;

        // Compost Settings
        public final ModConfigSpec.DoubleValue erbapipaSeedsCompostChance;
        public final ModConfigSpec.DoubleValue erbapipaCompostChance;

        // Gameplay Settings
        public final ModConfigSpec.BooleanValue enableTallCropMechanics;
        public final ModConfigSpec.BooleanValue breakingUpperBreaksLower;
        public final ModConfigSpec.DoubleValue fertilizerEffectiveness;

        // Performance Settings
        public final ModConfigSpec.DoubleValue tickRateMultiplier;
        public final ModConfigSpec.IntValue maxDryingRacksPerChunk;

        // Integration Settings
        public final ModConfigSpec.BooleanValue jeiIntegration;

        // Debug Settings
        public final ModConfigSpec.BooleanValue enableDebugLogging;
        public final ModConfigSpec.BooleanValue showGrowthParticles;

        Common(ModConfigSpec.Builder builder) {
            builder.comment("EyPipes Common Configuration")
                   .push("common");

            // Crop Settings
            builder.comment("Crop Growth Settings")
                   .push("crop");

            growthChance = builder
                    .comment("Chance for the crop to grow each random tick (0.0 - 1.0)")
                    .defineInRange("growthChance", 0.25, 0.0, 1.0);

            maxAge = builder
                    .comment("Maximum age/growth stage of the crop")
                    .defineInRange("maxAge", 7, 1, 15);

            growUpperAge = builder
                    .comment("Age at which the upper part of the crop can start growing")
                    .defineInRange("growUpperAge", 4, 0, 7);

            requiresSupport = builder
                    .comment("Whether the crop requires a support block below")
                    .define("requiresSupport", true);

            builder.pop();

            // Drying Rack Settings
            builder.comment("Drying Rack Settings")
                   .push("dryingRack");

            dryingTimeTicks = builder
                    .comment("Time in ticks to dry erbapipa into erbapipa_dried (20 ticks = 1 second)")
                    .defineInRange("dryingTimeTicks", 4000, 100, 72000);

            maxItems = builder
                    .comment("Maximum number of items the drying rack can hold")
                    .defineInRange("maxItems", 3, 1, 9);

            requiresSunlight = builder
                    .comment("Whether the drying rack requires direct sunlight to work")
                    .define("requiresSunlight", false);

            builder.pop();

            // Loot Settings
            builder.comment("Loot Drop Settings")
                   .push("loot");

            erbapipaDropRolls = builder
                    .comment("Number of rolls for erbapipa drops")
                    .defineInRange("erbapipaDropRolls", 0.8, 0.0, 10.0);

            seedsBonusRolls = builder
                    .comment("Bonus rolls for seed drops")
                    .defineInRange("seedsBonusRolls", 0.245, 0.0, 10.0);

            seedsFortuneProbability = builder
                    .comment("Fortune enchantment probability multiplier for seeds")
                    .defineInRange("seedsFortuneProbability", 0.06, 0.0, 1.0);

            fortuneAffectsDrops = builder
                    .comment("Whether Fortune enchantment affects crop drops")
                    .define("fortuneAffectsDrops", true);

            erbapipaDropPercentage = builder
                    .comment("Percentage chance for erbapipa to drop (0-100)")
                    .defineInRange("erbapipaDropPercentage", 100.0, 0.0, 100.0);

            seedsDropPercentage = builder
                    .comment("Percentage chance for seeds to drop (0-100)")
                    .defineInRange("seedsDropPercentage", 35.0, 0.0, 100.0);

            builder.pop();

            // Compost Settings
            builder.comment("Composting Settings")
                   .push("compost");

            erbapipaSeedsCompostChance = builder
                    .comment("Composting chance for erbapipa seeds (0.0 - 1.0)")
                    .defineInRange("erbapipaSeedsCompostChance", 0.3, 0.0, 1.0);

            erbapipaCompostChance = builder
                    .comment("Composting chance for erbapipa (0.0 - 1.0)")
                    .defineInRange("erbapipaCompostChance", 0.65, 0.0, 1.0);

            builder.pop();

            // Gameplay Settings
            builder.comment("Gameplay Settings")
                   .push("gameplay");

            enableTallCropMechanics = builder
                    .comment("Enable the tall (2-block) crop mechanics")
                    .define("enableTallCropMechanics", true);

            breakingUpperBreaksLower = builder
                    .comment("Breaking the upper part of the crop breaks the lower part")
                    .define("breakingUpperBreaksLower", true);

            fertilizerEffectiveness = builder
                    .comment("Fertilizer (bonemeal) effectiveness multiplier")
                    .defineInRange("fertilizerEffectiveness", 1.0, 0.0, 10.0);

            builder.pop();

            // Performance Settings
            builder.comment("Performance Settings")
                   .push("performance");

            tickRateMultiplier = builder
                    .comment("Tick rate multiplier for mod mechanics")
                    .defineInRange("tickRateMultiplier", 1.0, 0.1, 10.0);

            maxDryingRacksPerChunk = builder
                    .comment("Maximum drying racks allowed per chunk (performance limit)")
                    .defineInRange("maxDryingRacksPerChunk", 64, 1, 256);

            builder.pop();

            // Integration Settings
            builder.comment("Mod Integration Settings")
                   .push("integration");

            jeiIntegration = builder
                    .comment("Enable JEI integration for recipe viewing")
                    .define("jeiIntegration", true);

            builder.pop();

            // Debug Settings
            builder.comment("Debug Settings")
                   .push("debug");

            enableDebugLogging = builder
                    .comment("Enable debug logging for troubleshooting")
                    .define("enableDebugLogging", false);

            showGrowthParticles = builder
                    .comment("Show particles when crops grow")
                    .define("showGrowthParticles", false);

            builder.pop();
            builder.pop();
        }
    }

    /**
     * Client configuration - Client-side settings
     */
    public static class Client {
        // Particle Offset Settings
        public final ModConfigSpec.DoubleValue particleOffsetThirdViewX;
        public final ModConfigSpec.DoubleValue particleOffsetThirdViewY;
        public final ModConfigSpec.DoubleValue particleOffsetThirdViewZ;

        // Animation Settings
        public final ModConfigSpec.DoubleValue smokeParticleScale;
        public final ModConfigSpec.IntValue smokeParticleLifetime;
        public final ModConfigSpec.BooleanValue enableEmberParticles;
        public final ModConfigSpec.BooleanValue enableAshParticles;
        public final ModConfigSpec.BooleanValue enableSmokeWisps;

        // Enhanced Particle Settings
        public final ModConfigSpec.BooleanValue enableSmokeRings;
        public final ModConfigSpec.BooleanValue enableSpiralSmoke;
        public final ModConfigSpec.IntValue spiralSmokeCount;
        public final ModConfigSpec.BooleanValue enableSparkParticles;
        public final ModConfigSpec.BooleanValue enableEnhancedExhale;

        Client(ModConfigSpec.Builder builder) {
            builder.comment("EyPipes Client Configuration")
                   .push("client");

            // Particle Offset Settings
            builder.comment("Particle Position Offsets (Third Person View)")
                   .push("particleOffsets");

            particleOffsetThirdViewX = builder
                    .comment("X offset for smoke particles in third person view")
                    .defineInRange("thirdViewX", 0.2, -2.0, 2.0);

            particleOffsetThirdViewY = builder
                    .comment("Y offset for smoke particles in third person view")
                    .defineInRange("thirdViewY", 0.4, -2.0, 2.0);

            particleOffsetThirdViewZ = builder
                    .comment("Z offset for smoke particles in third person view")
                    .defineInRange("thirdViewZ", 0.1, -2.0, 2.0);

            builder.pop();

            // Animation Settings
            builder.comment("Animation and Visual Settings")
                   .push("animation");

            smokeParticleScale = builder
                    .comment("Scale multiplier for smoke particles")
                    .defineInRange("smokeParticleScale", 1.0, 0.1, 5.0);

            smokeParticleLifetime = builder
                    .comment("Lifetime of smoke particles in ticks")
                    .defineInRange("smokeParticleLifetime", 40, 10, 200);

            enableEmberParticles = builder
                    .comment("Enable ember particles when smoking (enhanced effect)")
                    .define("enableEmberParticles", true);

            enableAshParticles = builder
                    .comment("Enable ash particles when stopping smoking (enhanced effect)")
                    .define("enableAshParticles", true);

            enableSmokeWisps = builder
                    .comment("Enable wispy smoke trail particles (enhanced effect)")
                    .define("enableSmokeWisps", true);

            builder.pop();

            // Enhanced Particle Settings
            builder.comment("Enhanced Particle Effects - Stunning Visual Effects")
                   .push("enhancedParticles");

            enableSmokeRings = builder
                    .comment("Enable classic smoke ring particles on exhale")
                    .define("enableSmokeRings", true);

            enableSpiralSmoke = builder
                    .comment("Enable 3D spiraling smoke helix effect (mesmerizing!)")
                    .define("enableSpiralSmoke", true);

            spiralSmokeCount = builder
                    .comment("Number of spiral smoke particles per exhale (more = more dramatic)")
                    .defineInRange("spiralSmokeCount", 5, 1, 15);

            enableSparkParticles = builder
                    .comment("Enable bright spark particles from pipe bowl (fiery effect)")
                    .define("enableSparkParticles", true);

            enableEnhancedExhale = builder
                    .comment("Use the enhanced exhale effect system (combines all effects)")
                    .define("enableEnhancedExhale", true);

            builder.pop();
            builder.pop();
        }
    }
}
