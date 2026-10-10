package frenk.eypipes;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.integration.epicfight.EpicFightCompat;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.recipe.ModRecipes;
import frenk.eypipes.registries.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//? if neoforge {
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
//?} elif forge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.MinecraftForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
*///?}

/**
 * EyPipes - A smoking pipe mod with custom crops, drying mechanics, and animated items.
 *
 * <p>This class holds what every loader shares; on NeoForge and Forge it is also the mod's entry
 * point. Fabric starts from {@code EyPipesFabric} and calls {@link #init()} from there.
 */
//? if neoforge || forge
@Mod(EyPipes.MOD_ID)
public class EyPipes {
    public static final String MOD_ID = "eypipes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** An id in the EyPipes namespace. */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Loads every registry class. Blocks come before items: on Fabric each entry is registered
     * as soon as it is declared, and block items need their block.
     */
    public static void init() {
        LOGGER.info("EyPipes initializing");
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModParticles.init();
        ModSounds.init();
        ModCreativeTabs.init();
        ModDataComponents.init();
        ModRecipes.init();
    }

    /** Runs once registries are filled: compostables need the registered items. */
    public static void commonSetup() {
        ModItems.registerCompostables();
        if (EpicFightCompat.isLoaded()) {
            LOGGER.info("Epic Fight detected - smoking animations will load with resources");
        }
        LOGGER.info("EyPipes common setup complete!");
    }

    //? if neoforge {
    public EyPipes(IEventBus modEventBus, ModContainer modContainer) {
        init();
        Registrar.registerAll(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, EyPipesConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, EyPipesConfig.CLIENT_SPEC);

        // Epic Fight optional integration (registers AnimationRegistryEvent listener)
        EpicFightCompat.init(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        NeoForge.EVENT_BUS.register(this);
    }
    //?} elif forge {
    /*public EyPipes() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        init();
        Registrar.registerAll(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EyPipesConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EyPipesConfig.CLIENT_SPEC);

        EpicFightCompat.init(modEventBus);

        modEventBus.addListener(this::onCommonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }
    *///?}

    //? if neoforge || forge {
    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(EyPipes::commonSetup);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        // Register /eypipes reload command
        frenk.eypipes.command.ReloadConfigCommand.register(event.getDispatcher());
    }
    //?}
}
