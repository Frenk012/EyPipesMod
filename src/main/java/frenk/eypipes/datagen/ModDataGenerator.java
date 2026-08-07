package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
//? if <1.21.2 {
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;
//?}

/**
 * Main data generator entry point for EyPipes mod.
 * Registers all data providers for block states, models, loot tables, recipes, and tags.
 *
 * <p>From 1.21.2 the event is split into a client half and a server half, providers are added
 * through {@code createProvider} rather than being constructed by hand, and
 * {@code ExistingFileHelper} no longer exists.
 */
@EventBusSubscriber(modid = EyPipes.MOD_ID)
public class ModDataGenerator {

    //? if <1.21.2 {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Block state and model providers
        generator.addProvider(event.includeClient(),
                new ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(),
                new ModItemModelProvider(packOutput, existingFileHelper));

        // Loot table provider
        generator.addProvider(event.includeServer(),
                new ModLootTableProvider(packOutput, lookupProvider));

        // Recipe provider
        generator.addProvider(event.includeServer(),
                new ModRecipeProvider(packOutput, lookupProvider));

        // Tag providers
        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);
        generator.addProvider(event.includeServer(),
                new ModItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));

        EyPipes.LOGGER.info("EyPipes data generation registered");
    }
    //?} else {
    /*@SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        // One provider now emits blockstates, block models, item models and the
        // assets/<ns>/items/ client-item definitions together.
        event.createProvider(ModModelProvider::new);

        EyPipes.LOGGER.info("EyPipes client data generation registered");
    }

    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        event.createProvider(ModLootTableProvider::new);
        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(ModBlockTagProvider::new);
        event.createProvider(ModItemTagProvider::new);

        EyPipes.LOGGER.info("EyPipes server data generation registered");
    }
    *///?}
}
