package frenk.eypipes.registries;

import frenk.eypipes.EyPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for EyPipes creative mode tab using NeoForge DeferredRegister
 */
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EyPipes.MOD_ID);

    // Main EyPipes creative tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EYPIPES_TAB =
            CREATIVE_MODE_TABS.register("eypipes_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + EyPipes.MOD_ID + ".eypipes_tab"))
                            .icon(() -> new ItemStack(ModItems.PIPE.get()))
                            .displayItems((parameters, output) -> {
                                // Add all mod items to the creative tab
                                output.accept(ModItems.ERBAPIPA_SEEDS.get());
                                output.accept(ModItems.ERBAPIPA.get());
                                output.accept(ModItems.ERBAPIPA_DRIED.get());
                                output.accept(ModItems.ERBAPIPA_CUTTED.get());
                                output.accept(ModItems.PIPE.get());
                                output.accept(ModItems.CIGAR.get());
                                output.accept(ModItems.DRYING_RACK_ITEM.get());
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

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
        EyPipes.LOGGER.info("Registering EyPipes Creative Tabs");
    }
}
