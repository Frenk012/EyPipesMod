package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for EyPipes creative mode tab
 */
public class ModCreativeTabs {
    public static final Registrar<CreativeModeTab> CREATIVE_MODE_TABS = Registrar.create(Registries.CREATIVE_MODE_TAB);

    // Main EyPipes creative tab
    public static final RegistryEntry<CreativeModeTab> EYPIPES_TAB =
            CREATIVE_MODE_TABS.register("eypipes_tab",
                    // Vanilla's builder takes a row and column; Fabric API and (Neo)Forge add their own
                    () -> /*? if fabric {*//*net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup.builder()*//*?} else {*/CreativeModeTab.builder()/*?}*/
                            .title(Component.translatable("itemGroup." + EyPipes.MOD_ID + ".eypipes_tab"))
                            .icon(() -> new ItemStack(ModItems.PIPE.get()))
                            .displayItems((parameters, output) -> {
                                // Add all mod items to the creative tab
                                // Erbapipa (original herb)
                                output.accept(ModItems.ERBAPIPA_SEEDS.get());
                                output.accept(ModItems.ERBAPIPA.get());
                                output.accept(ModItems.ERBAPIPA_DRIED.get());
                                output.accept(ModItems.ERBAPIPA_CUTTED.get());
                                output.accept(ModItems.ERBAPIPA_BUNDLE_ITEM.get());
                                // Valeriana (calming herb)
                                output.accept(ModItems.VALERIANA_SEEDS.get());
                                output.accept(ModItems.VALERIANA.get());
                                output.accept(ModItems.VALERIANA_DRIED.get());
                                output.accept(ModItems.VALERIANA_CUTTED.get());
                                output.accept(ModItems.VALERIANA_BUNDLE_ITEM.get());
                                // Ginseng (energizing herb)
                                output.accept(ModItems.GINSENG_SEEDS.get());
                                output.accept(ModItems.GINSENG.get());
                                output.accept(ModItems.GINSENG_DRIED.get());
                                output.accept(ModItems.GINSENG_CUTTED.get());
                                output.accept(ModItems.GINSENG_BUNDLE_ITEM.get());
                                // Salvia (vision herb)
                                output.accept(ModItems.SALVIA_SEEDS.get());
                                output.accept(ModItems.SALVIA.get());
                                output.accept(ModItems.SALVIA_DRIED.get());
                                output.accept(ModItems.SALVIA_CUTTED.get());
                                output.accept(ModItems.SALVIA_BUNDLE_ITEM.get());
                                // Smoking items
                                output.accept(ModItems.PIPE.get());
                                output.accept(ModItems.CIGAR.get());
                                output.accept(ModItems.DRYING_RACK_ITEM.get());
                                output.accept(ModItems.TOBACCO_JAR_ITEM.get());
                                output.accept(ModItems.PIPE_RACK_ITEM.get());
                                output.accept(ModItems.CUTTING_BOARD_ITEM.get());
                                output.accept(ModItems.KNIFE.get());
                                // Pipe variants
                                output.accept(ModItems.WOODEN_PIPE.get());
                                output.accept(ModItems.CLAY_PIPE.get());
                                output.accept(ModItems.CORN_COB_PIPE.get());
                                output.accept(ModItems.MEERSCHAUM_PIPE.get());
                                output.accept(ModItems.BRIAR_PIPE.get());
                                output.accept(ModItems.CHERRY_PIPE.get());
                                output.accept(ModItems.CALABASH_PIPE.get());
                                output.accept(ModItems.CHURCHWARD_PIPE.get());
                                output.accept(ModItems.BENT_PIPE.get());
                            })
                            .build());

    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
        EyPipes.LOGGER.info("Registering EyPipes Creative Tabs");
    }
}
