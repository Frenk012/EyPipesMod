package frenk.eypipes.registries;

import com.mojang.serialization.Codec;
import frenk.eypipes.EyPipes;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for custom data components used by EyPipes.
 * Data components replace NBT tags in NeoForge 1.21.1.
 */
public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EyPipes.MOD_ID);

    /**
     * Fermentation level of tobacco/herbs.
     * 0 = Fresh (just harvested)
     * 1 = Dried (after drying rack)
     * 2 = Aged (1 day in tobacco jar)
     * 3 = Fermented (3 days in tobacco jar)
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FERMENTATION_LEVEL =
            DATA_COMPONENTS.register("fermentation_level",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
                            .build());

    /**
     * Game time when fermentation started in the tobacco jar.
     * Used to calculate how long the item has been fermenting.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> FERMENTATION_START =
            DATA_COMPONENTS.register("fermentation_start",
                    () -> DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(ByteBufCodecs.VAR_LONG)
                            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Data Components");
    }

    // Quality level constants
    public static final int QUALITY_FRESH = 0;
    public static final int QUALITY_DRIED = 1;
    public static final int QUALITY_AGED = 2;
    public static final int QUALITY_FERMENTED = 3;

    /**
     * Get the effect duration multiplier based on fermentation level.
     * Fresh: 0.5x, Dried: 1.0x, Aged: 1.5x, Fermented: 2.0x
     */
    public static float getQualityMultiplier(int fermentationLevel) {
        return switch (fermentationLevel) {
            case QUALITY_FRESH -> 0.5f;
            case QUALITY_AGED -> 1.5f;
            case QUALITY_FERMENTED -> 2.0f;
            default -> 1.0f; // QUALITY_DRIED
        };
    }

    /**
     * Get display name for fermentation level.
     */
    public static String getQualityName(int fermentationLevel) {
        return switch (fermentationLevel) {
            case QUALITY_FRESH -> "Fresh";
            case QUALITY_AGED -> "Aged";
            case QUALITY_FERMENTED -> "Fermented";
            default -> "Dried";
        };
    }
}
