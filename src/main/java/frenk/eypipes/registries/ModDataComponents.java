package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
//? if >=1.20.5 {
import com.mojang.serialization.Codec;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
//?}

/**
 * The values EyPipes keeps on item stacks: data components from 1.20.5, NBT keys before.
 */
public class ModDataComponents {
    //? if >=1.20.5 {
    public static final Registrar<DataComponentType<?>> DATA_COMPONENTS = Registrar.create(Registries.DATA_COMPONENT_TYPE);

    private static final RegistryEntry<DataComponentType<Integer>> FERMENTATION_LEVEL_TYPE =
            DATA_COMPONENTS.register("fermentation_level",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
                            .build());

    private static final RegistryEntry<DataComponentType<Long>> FERMENTATION_START_TYPE =
            DATA_COMPONENTS.register("fermentation_start",
                    () -> DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(ByteBufCodecs.VAR_LONG)
                            .build());
    //?}

    /**
     * Fermentation level of tobacco/herbs.
     * 0 = Fresh (just harvested)
     * 1 = Dried (after drying rack)
     * 2 = Aged (1 day in tobacco jar)
     * 3 = Fermented (3 days in tobacco jar)
     */
    public static final ItemValue<Integer> FERMENTATION_LEVEL =
            //? if >=1.20.5 {
            ItemValue.component(FERMENTATION_LEVEL_TYPE);
            //?} else
            //ItemValue.intTag("eypipes:fermentation_level");

    /**
     * Game time when fermentation started in the tobacco jar.
     * Used to calculate how long the item has been fermenting.
     */
    public static final ItemValue<Long> FERMENTATION_START =
            //? if >=1.20.5 {
            ItemValue.component(FERMENTATION_START_TYPE);
            //?} else
            //ItemValue.longTag("eypipes:fermentation_start");

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
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
