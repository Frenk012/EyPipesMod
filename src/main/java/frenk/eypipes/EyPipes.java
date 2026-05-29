package frenk.eypipes;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.recipe.ModRecipes;
import frenk.eypipes.registries.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EyPipes - A smoking pipe mod with custom crops, drying mechanics, and animated items.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1
 */
@Mod(EyPipes.MOD_ID)
public class EyPipes {
    public static final String MOD_ID = "eypipes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public EyPipes(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("EyPipes initializing for NeoForge 1.21.1!");

        // Register all deferred registers to the mod event bus
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModParticles.register(modEventBus);
        ModSounds.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModRecipes.register(modEventBus);

        // Register mod configuration
        modContainer.registerConfig(ModConfig.Type.COMMON, EyPipesConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, EyPipesConfig.CLIENT_SPEC);

        // Epic Fight optional integration (registers AnimationRegistryEvent listener)
        frenk.eypipes.integration.epicfight.EpicFightCompat.init(modEventBus);

        // Register common setup event
        modEventBus.addListener(this::commonSetup);

        // Register server events on NeoForge bus
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("EyPipes common setup complete!");
            registerCompostables();
            if (frenk.eypipes.integration.epicfight.EpicFightCompat.isLoaded()) {
                LOGGER.info("Epic Fight detected - smoking animations will load with resources");
            }
        });
    }

    private void registerCompostables() {
        // Compostables are registered via ComposterBlock.add() in NeoForge
        // This will be done after blocks and items are registered
        ModItems.registerCompostables();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("EyPipes server starting!");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // Register /eypipes reload command
        frenk.eypipes.command.ReloadConfigCommand.register(event.getDispatcher());
    }
}
