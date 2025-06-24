package frenk.eypipes.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import frenk.eypipes.EyPipes;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EyPipesConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("eypipes_config.json");
    
    // Crop Settings
    public static double GROWTH_CHANCE = 0.25;
    public static int MAX_AGE = 7;
    public static int GROW_UPPER_AGE = 4;
    public static boolean REQUIRES_SUPPORT = true;
    
    // Drying Rack Settings
    public static int DRYING_TIME_TICKS = 6000;
    public static int MAX_ITEMS = 3;
    public static boolean REQUIRES_SUNLIGHT = false;
    
    // Loot Settings
    public static double ERBAPIPA_DROP_ROLLS = 0.8;
    public static double SEEDS_BONUS_ROLLS = 0.245;  // Reduced by 65% (0.7 * 0.35)
    public static double SEEDS_FORTUNE_PROBABILITY = 0.06;  // Reduced by 65% (0.17142858 * 0.35)
    public static boolean FORTUNE_AFFECTS_DROPS = true;
    public static double ERBAPIPA_DROP_PERCENTAGE = 100.0;
    public static double SEEDS_DROP_PERCENTAGE = 35.0;  // Reduced by 65% (100.0 * 0.35)
    
    // Recipe Settings
    public static int CUTTING_OUTPUT_COUNT = 2;
    public static boolean ENABLE_KNIFE_TAG_SUPPORT = true;
    
    // Compost Settings
    public static float ERBAPIPA_SEEDS_COMPOST_CHANCE = 0.3f;
    public static float ERBAPIPA_COMPOST_CHANCE = 0.65f;
    
    // Gameplay Settings
    public static boolean ENABLE_TALL_CROP_MECHANICS = true;
    public static boolean BREAKING_UPPER_BREAKS_LOWER = true;
    public static double FERTILIZER_EFFECTIVENESS = 1.0;
    
    // Performance Settings
    public static double TICK_RATE_MULTIPLIER = 1.0;
    public static int MAX_DRYING_RACKS_PER_CHUNK = 64;
    
    // Integration Settings
    public static boolean FARMERS_DELIGHT_INTEGRATION = true;
    public static boolean JEI_INTEGRATION = true;
    
    // Debug Settings
    public static boolean ENABLE_DEBUG_LOGGING = false;
    public static boolean SHOW_GROWTH_PARTICLES = false;
    
    // Animation Settings
    public static double ANIMATION_CURVE_MULTIPLIER = 1.0;
    public static int ANIMATION_DELAY_TICKS = 1;
    public static float ANIMATION_DURATION_SECONDS = 1.0f;
    public static boolean ENABLE_ROTATION_ANIMATION = true;
    
    // First Person Animation Settings
    public static float FIRST_PERSON_CURVE_MULTIPLIER = 0.2f;
    public static float FIRST_PERSON_Y_TRANSLATION = 0.3f;
    public static float FIRST_PERSON_Z_TRANSLATION = 0.25f;
    public static float FIRST_PERSON_X_TRANSLATION = 0.0f;
    public static float FIRST_PERSON_X_ROTATION = 25.0f;
    public static float FIRST_PERSON_Y_ROTATION = 20.0f;
    public static float FIRST_PERSON_Z_ROTATION = 20.0f;
    public static int FIRST_PERSON_DELAY_TICKS = 1;
    public static float FIRST_PERSON_DURATION_SECONDS = 1.0f;
    public static boolean FIRST_PERSON_ENABLE_ROTATION = true;
    
    // Third Person Animation Settings
    public static float THIRD_PERSON_CURVE_MULTIPLIER = 1.0f;
    public static float THIRD_PERSON_Y_TRANSLATION = 0.05f;
    public static float THIRD_PERSON_Z_TRANSLATION = 0.15f;
    public static float THIRD_PERSON_X_TRANSLATION = 0.0f;
    public static float THIRD_PERSON_X_ROTATION = 8.0f;
    public static float THIRD_PERSON_Y_ROTATION = 5.0f;
    public static float THIRD_PERSON_Z_ROTATION = -3.0f;
    public static int THIRD_PERSON_DELAY_TICKS = 1;
    public static float THIRD_PERSON_DURATION_SECONDS = 1.5f;
    public static boolean THIRD_PERSON_ENABLE_ROTATION = true;
    
    // Position Offset Settings
    public static float OFFSET_HEAD_X = 1.0f;
    public static float OFFSET_HEAD_Y = 0.0f;
    public static float OFFSET_HEAD_Z = 0.0f;
    public static float OFFSET_FIXED_X = -0.5f;
    public static float OFFSET_FIXED_Y = 0.0f;
    public static float OFFSET_FIXED_Z = 0.0f;
    public static float OFFSET_THIRD_PERSON_LEFT_X = 0.25f;
    public static float OFFSET_THIRD_PERSON_LEFT_Y = 0.0f;
    public static float OFFSET_THIRD_PERSON_LEFT_Z = 0.0f;
    public static float OFFSET_FIRST_PERSON_RIGHT_X = 0.7f;
    public static float OFFSET_FIRST_PERSON_RIGHT_Y = 0.2f;
    public static float OFFSET_FIRST_PERSON_RIGHT_Z = 0.0f;
    
    public static void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
                return;
            }
            
            JsonObject config = JsonParser.parseReader(new FileReader(CONFIG_PATH.toFile())).getAsJsonObject();
            EyPipes.LOGGER.info("Successfully loaded config file from: " + CONFIG_PATH.toString());
            
            // Load crop settings
            if (config.has("crop_settings")) {
                JsonObject cropSettings = config.getAsJsonObject("crop_settings");
                GROWTH_CHANCE = getDoubleOrDefault(cropSettings, "growth_chance", 0.25);
                MAX_AGE = getIntOrDefault(cropSettings, "max_age", 7);
                GROW_UPPER_AGE = getIntOrDefault(cropSettings, "grow_upper_age", 4);
                REQUIRES_SUPPORT = getBooleanOrDefault(cropSettings, "requires_support", true);
            }
            
            // Load drying rack settings
            if (config.has("drying_rack_settings")) {
                JsonObject dryingSettings = config.getAsJsonObject("drying_rack_settings");
                DRYING_TIME_TICKS = getIntOrDefault(dryingSettings, "drying_time_ticks", 6000);
                MAX_ITEMS = getIntOrDefault(dryingSettings, "max_items", 3);
                REQUIRES_SUNLIGHT = getBooleanOrDefault(dryingSettings, "requires_sunlight", false);
            }
            
            // Load loot settings
            if (config.has("loot_settings")) {
                JsonObject lootSettings = config.getAsJsonObject("loot_settings");
                ERBAPIPA_DROP_ROLLS = getDoubleOrDefault(lootSettings, "erbapipa_drop_rolls", 0.8);
                SEEDS_BONUS_ROLLS = getDoubleOrDefault(lootSettings, "seeds_bonus_rolls", 0.245);  // Reduced by 65%
                SEEDS_FORTUNE_PROBABILITY = getDoubleOrDefault(lootSettings, "seeds_fortune_probability", 0.06);  // Reduced by 65%
                FORTUNE_AFFECTS_DROPS = getBooleanOrDefault(lootSettings, "fortune_affects_drops", true);
                ERBAPIPA_DROP_PERCENTAGE = getDoubleOrDefault(lootSettings, "erbapipa_drop_percentage", 100.0);
                SEEDS_DROP_PERCENTAGE = getDoubleOrDefault(lootSettings, "seeds_drop_percentage", 35.0);  // Reduced by 65%
            }
            
            // Load recipe settings
            if (config.has("recipe_settings")) {
                JsonObject recipeSettings = config.getAsJsonObject("recipe_settings");
                CUTTING_OUTPUT_COUNT = getIntOrDefault(recipeSettings, "cutting_output_count", 2);
                ENABLE_KNIFE_TAG_SUPPORT = getBooleanOrDefault(recipeSettings, "enable_knife_tag_support", true);
            }
            
            // Load compost settings
            if (config.has("compost_settings")) {
                JsonObject compostSettings = config.getAsJsonObject("compost_settings");
                ERBAPIPA_SEEDS_COMPOST_CHANCE = (float) getDoubleOrDefault(compostSettings, "erbapipa_seeds_compost_chance", 0.3);
                ERBAPIPA_COMPOST_CHANCE = (float) getDoubleOrDefault(compostSettings, "erbapipa_compost_chance", 0.65);
            }
            
            // Load gameplay settings
            if (config.has("gameplay_settings")) {
                JsonObject gameplaySettings = config.getAsJsonObject("gameplay_settings");
                ENABLE_TALL_CROP_MECHANICS = getBooleanOrDefault(gameplaySettings, "enable_tall_crop_mechanics", true);
                BREAKING_UPPER_BREAKS_LOWER = getBooleanOrDefault(gameplaySettings, "breaking_upper_breaks_lower", true);
                FERTILIZER_EFFECTIVENESS = getDoubleOrDefault(gameplaySettings, "fertilizer_effectiveness", 1.0);
            }
            
            // Load performance settings
            if (config.has("performance_settings")) {
                JsonObject performanceSettings = config.getAsJsonObject("performance_settings");
                TICK_RATE_MULTIPLIER = getDoubleOrDefault(performanceSettings, "tick_rate_multiplier", 1.0);
                MAX_DRYING_RACKS_PER_CHUNK = getIntOrDefault(performanceSettings, "max_drying_racks_per_chunk", 64);
            }
            
            // Load integration settings
            if (config.has("integration_settings")) {
                JsonObject integrationSettings = config.getAsJsonObject("integration_settings");
                FARMERS_DELIGHT_INTEGRATION = getBooleanOrDefault(integrationSettings, "farmers_delight_integration", true);
                JEI_INTEGRATION = getBooleanOrDefault(integrationSettings, "jei_integration", true);
            }
            
            // Load debug settings
            if (config.has("debug_settings")) {
                JsonObject debugSettings = config.getAsJsonObject("debug_settings");
                ENABLE_DEBUG_LOGGING = getBooleanOrDefault(debugSettings, "enable_debug_logging", false);
                SHOW_GROWTH_PARTICLES = getBooleanOrDefault(debugSettings, "show_growth_particles", false);
            }
            
            // Load animation settings
            if (config.has("animation_settings")) {
                JsonObject animationSettings = config.getAsJsonObject("animation_settings");
                ANIMATION_CURVE_MULTIPLIER = getDoubleOrDefault(animationSettings, "curve_multiplier", 1.0);
                ANIMATION_DELAY_TICKS = getIntOrDefault(animationSettings, "delay_ticks", 1);
                ANIMATION_DURATION_SECONDS = (float) getDoubleOrDefault(animationSettings, "duration_seconds", 1.0);
                ENABLE_ROTATION_ANIMATION = getBooleanOrDefault(animationSettings, "enable_rotation", true);
                
                // Load first person animation settings
                if (animationSettings.has("first_person")) {
                    JsonObject firstPersonSettings = animationSettings.getAsJsonObject("first_person");
                    FIRST_PERSON_CURVE_MULTIPLIER = (float) getDoubleOrDefault(firstPersonSettings, "curve_multiplier", 1.0);
                    FIRST_PERSON_Y_TRANSLATION = (float) getDoubleOrDefault(firstPersonSettings, "y_translation", 0.1);
                    FIRST_PERSON_Z_TRANSLATION = (float) getDoubleOrDefault(firstPersonSettings, "z_translation", 0.25);
                    FIRST_PERSON_X_TRANSLATION = (float) getDoubleOrDefault(firstPersonSettings, "x_translation", 0.0);
                    FIRST_PERSON_X_ROTATION = (float) getDoubleOrDefault(firstPersonSettings, "x_rotation", 15.0);
                    FIRST_PERSON_Y_ROTATION = (float) getDoubleOrDefault(firstPersonSettings, "y_rotation", 10.0);
                    FIRST_PERSON_Z_ROTATION = (float) getDoubleOrDefault(firstPersonSettings, "z_rotation", -5.0);
                    FIRST_PERSON_DELAY_TICKS = getIntOrDefault(firstPersonSettings, "delay_ticks", 1);
                    FIRST_PERSON_DURATION_SECONDS = (float) getDoubleOrDefault(firstPersonSettings, "duration_seconds", 1.0);
                    FIRST_PERSON_ENABLE_ROTATION = getBooleanOrDefault(firstPersonSettings, "enable_rotation", true);
                }
                
                // Load third person animation settings
                if (animationSettings.has("third_person")) {
                    JsonObject thirdPersonSettings = animationSettings.getAsJsonObject("third_person");
                    THIRD_PERSON_CURVE_MULTIPLIER = (float) getDoubleOrDefault(thirdPersonSettings, "curve_multiplier", 1.0);
                    THIRD_PERSON_Y_TRANSLATION = (float) getDoubleOrDefault(thirdPersonSettings, "y_translation", 0.05);
                    THIRD_PERSON_Z_TRANSLATION = (float) getDoubleOrDefault(thirdPersonSettings, "z_translation", 0.15);
                    THIRD_PERSON_X_TRANSLATION = (float) getDoubleOrDefault(thirdPersonSettings, "x_translation", 0.0);
                    THIRD_PERSON_X_ROTATION = (float) getDoubleOrDefault(thirdPersonSettings, "x_rotation", 8.0);
                    THIRD_PERSON_Y_ROTATION = (float) getDoubleOrDefault(thirdPersonSettings, "y_rotation", 5.0);
                    THIRD_PERSON_Z_ROTATION = (float) getDoubleOrDefault(thirdPersonSettings, "z_rotation", -3.0);
                    THIRD_PERSON_DELAY_TICKS = getIntOrDefault(thirdPersonSettings, "delay_ticks", 1);
                    THIRD_PERSON_DURATION_SECONDS = (float) getDoubleOrDefault(thirdPersonSettings, "duration_seconds", 1.5);
                    THIRD_PERSON_ENABLE_ROTATION = getBooleanOrDefault(thirdPersonSettings, "enable_rotation", true);
                }
                
                // Load position offset settings
                if (animationSettings.has("position_offsets")) {
                    JsonObject offsetSettings = animationSettings.getAsJsonObject("position_offsets");
                    OFFSET_HEAD_X = (float) getDoubleOrDefault(offsetSettings, "head_x", 1.0);
                    OFFSET_HEAD_Y = (float) getDoubleOrDefault(offsetSettings, "head_y", 0.0);
                    OFFSET_HEAD_Z = (float) getDoubleOrDefault(offsetSettings, "head_z", 0.0);
                    OFFSET_FIXED_X = (float) getDoubleOrDefault(offsetSettings, "fixed_x", -0.5);
                    OFFSET_FIXED_Y = (float) getDoubleOrDefault(offsetSettings, "fixed_y", 0.0);
                    OFFSET_FIXED_Z = (float) getDoubleOrDefault(offsetSettings, "fixed_z", 0.0);
                    OFFSET_THIRD_PERSON_LEFT_X = (float) getDoubleOrDefault(offsetSettings, "third_person_left_x", 0.25);
                    OFFSET_THIRD_PERSON_LEFT_Y = (float) getDoubleOrDefault(offsetSettings, "third_person_left_y", 0.0);
                    OFFSET_THIRD_PERSON_LEFT_Z = (float) getDoubleOrDefault(offsetSettings, "third_person_left_z", 0.0);
                    OFFSET_FIRST_PERSON_RIGHT_X = (float) getDoubleOrDefault(offsetSettings, "first_person_right_x", 0.7);
                    OFFSET_FIRST_PERSON_RIGHT_Y = (float) getDoubleOrDefault(offsetSettings, "first_person_right_y", 0.2);
                    OFFSET_FIRST_PERSON_RIGHT_Z = (float) getDoubleOrDefault(offsetSettings, "first_person_right_z", 0.0);
                }
                
                EyPipes.LOGGER.info("Loaded animation settings - FP Y_ROT: " + FIRST_PERSON_Y_ROTATION + ", TP Y_ROT: " + THIRD_PERSON_Y_ROTATION + ", DELAY: " + ANIMATION_DELAY_TICKS);
            }
            
            EyPipes.LOGGER.info("EyPipes config loaded successfully!");
            
        } catch (Exception e) {
            EyPipes.LOGGER.error("Failed to load EyPipes config, using defaults", e);
        }
    }
    
    private static void createDefaultConfig() {
        try {
            // Copy the default config from resources to config directory
            Path resourceConfig = FabricLoader.getInstance().getModContainer("eypipes")
                .get().findPath("eypipes_config.json").orElse(null);
            
            if (resourceConfig != null && Files.exists(resourceConfig)) {
                Files.copy(resourceConfig, CONFIG_PATH);
                EyPipes.LOGGER.info("Created default EyPipes config file");
            } else {
                EyPipes.LOGGER.warn("Could not find default config in resources, using hardcoded defaults");
            }
        } catch (IOException e) {
            EyPipes.LOGGER.error("Failed to create default config file", e);
        }
    }
    
    public static void reloadConfig() {
        try {
            EyPipes.LOGGER.info("Reloading EyPipes configuration...");
            loadConfig();
            
            // Re-register compostables with new values
            EyPipes.registerCompostables();
            // Note: Loot table modifications are applied dynamically at runtime
            // so they will use the new config values immediately
            
            EyPipes.LOGGER.info("Configuration reloaded successfully!");
        } catch (Exception e) {
            EyPipes.LOGGER.error("Failed to reload configuration: " + e.getMessage());
        }
    }
    
    private static double getDoubleOrDefault(JsonObject obj, String key, double defaultValue) {
        return obj.has(key) ? obj.get(key).getAsDouble() : defaultValue;
    }
    
    private static int getIntOrDefault(JsonObject obj, String key, int defaultValue) {
        return obj.has(key) ? obj.get(key).getAsInt() : defaultValue;
    }
    
    private static boolean getBooleanOrDefault(JsonObject obj, String key, boolean defaultValue) {
        return obj.has(key) ? obj.get(key).getAsBoolean() : defaultValue;
    }
}