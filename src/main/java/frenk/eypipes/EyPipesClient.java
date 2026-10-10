package frenk.eypipes;

import frenk.eypipes.client.curios.CigarCuriosRenderer;
import frenk.eypipes.client.curios.PipeCuriosRenderer;
//? if <1.21.9
import frenk.eypipes.client.renderer.CigarItemRenderer;
import frenk.eypipes.client.renderer.CuttingBoardRenderer;
import frenk.eypipes.client.renderer.DryingRackRenderer;
//? if <1.21.9
import frenk.eypipes.client.renderer.PipeGeoRenderer;
import frenk.eypipes.client.renderer.PipeRackRenderer;
import frenk.eypipes.particle.RingOfSmokeParticle;
import frenk.eypipes.particle.EmberParticle;
import frenk.eypipes.particle.SpiralSmokeParticle;
import frenk.eypipes.particle.SparkParticle;
import frenk.eypipes.particle.SmokeStreamParticle;
import frenk.eypipes.particle.SteamParticle;
import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModBlocks;
import frenk.eypipes.registries.ModItems;
import frenk.eypipes.registries.ModParticles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
//? if <1.21.9
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
//? if forge {
/*import net.neoforged.fml.common.Mod;
*///?} else
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
//? if <1.21.9
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
//? if neoforge && <1.21.9
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import frenk.eypipes.platform.RegistryEntry;
//? if <1.21.9
import software.bernie.geckolib.renderer.GeoItemRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;


/**
 * Client-side initialization and event handling for EyPipes mod.
 * Uses NeoForge event subscribers instead of Fabric's ClientModInitializer.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
//? if forge {
/*@Mod.EventBusSubscriber(modid = EyPipes.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
*///?} else
@EventBusSubscriber(modid = EyPipes.MOD_ID, value = Dist.CLIENT)
public class EyPipesClient {

    /**
     * Client setup event - runs after registries are complete.
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Set crop block to use cutout render layer (for transparency)
            //? if <1.21.9 {
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ERBAPIPA_CROP.get(), RenderType.cutout());
            //?} else
            /*ItemBlockRenderTypes.setRenderLayer(ModBlocks.ERBAPIPA_CROP.get(), net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT);*/

            // Curios renderers: every pipe variant shares one, the cigar has its own
            for (RegistryEntry<Item> pipe : ModItems.PIPES) {
                CuriosRendererRegistry.register(pipe.get(), PipeCuriosRenderer::new);
            }
            CuriosRendererRegistry.register(ModItems.CIGAR.get(), CigarCuriosRenderer::new);
        });

        EyPipes.LOGGER.info("EyPipes client setup complete");
    }

    /**
     * Register block entity renderers.
     */
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Register drying rack block entity renderer
        event.registerBlockEntityRenderer(ModBlockEntities.DRYING_RACK.get(), DryingRackRenderer::new);

        // Register pipe rack block entity renderer
        event.registerBlockEntityRenderer(ModBlockEntities.PIPE_RACK.get(), PipeRackRenderer::new);

        // Register cutting board block entity renderer
        event.registerBlockEntityRenderer(ModBlockEntities.CUTTING_BOARD.get(), CuttingBoardRenderer::new);

        EyPipes.LOGGER.debug("Registered EyPipes block entity renderers (3 total)");
    }

    /**
     * Register client extensions for GeckoLib item renderers.
     */
    //? if neoforge && <1.21.9 {
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        // Every pipe variant shares one data-driven renderer; the asset name is the registry name
        for (RegistryEntry<Item> pipe : ModItems.PIPES) {
            String name = pipe.getId().getPath();
            event.registerItem(createPipeExtension(() -> name), pipe.get());
        }

        event.registerItem(createCigarExtension(), ModItems.CIGAR.get());

        EyPipes.LOGGER.debug("Registered EyPipes GeckoLib item renderers ({} total)", ModItems.PIPES.size() + 1);
    }
    //?}

    //? if <1.21.9 {
    /**
     * Item extension that renders the cigar through GeckoLib. NeoForge registers it from
     * {@code RegisterClientExtensionsEvent}; Forge 1.20.1 asks the item for it instead.
     */
    public static IClientItemExtensions createCigarExtension() {
        return new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new CigarItemRenderer();
                }
                return renderer;
            }
        };
    }

    /**
     * Helper method to create IClientItemExtensions for a pipe variant.
     *
     * @param name the pipe's registry name, also its GeckoLib asset base name. Read lazily: on
     *             Forge the extension is created while the item is constructed, before it has one.
     */
    public static IClientItemExtensions createPipeExtension(java.util.function.Supplier<String> name) {
        return new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new PipeGeoRenderer(name.get());
                }
                return renderer;
            }
        };
    }
    //?}

    /**
     * Register particle providers/factories.
     */
    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        // Register particle factories
        event.registerSpriteSet(ModParticles.RING_OF_SMOKE.get(), RingOfSmokeParticle.Provider::new);
        event.registerSpriteSet(ModParticles.EMBER.get(), EmberParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SPIRAL_SMOKE.get(), SpiralSmokeParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SPARK.get(), SparkParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SMOKE_STREAM.get(), SmokeStreamParticle.Provider::new);
        event.registerSpriteSet(ModParticles.STEAM.get(), SteamParticle.Provider::new);

        EyPipes.LOGGER.debug("Registered EyPipes particle providers (6 types)");
    }
}
