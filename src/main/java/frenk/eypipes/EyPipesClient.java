package frenk.eypipes;

import frenk.eypipes.client.curios.CigarCuriosRenderer;
import frenk.eypipes.client.curios.PipeCuriosRenderer;
import frenk.eypipes.client.renderer.CigarItemRenderer;
import frenk.eypipes.client.renderer.DryingRackRenderer;
import frenk.eypipes.client.renderer.PipeItemRenderer;
import frenk.eypipes.client.renderer.PipeRackRenderer;
import frenk.eypipes.client.renderer.WoodenPipeRenderer;
import frenk.eypipes.client.renderer.ClayPipeRenderer;
import frenk.eypipes.client.renderer.CornCobPipeRenderer;
import frenk.eypipes.client.renderer.MeerschaumPipeRenderer;
import frenk.eypipes.client.renderer.BriarPipeRenderer;
import frenk.eypipes.client.renderer.CherryPipeRenderer;
import frenk.eypipes.client.renderer.CalabashPipeRenderer;
import frenk.eypipes.client.renderer.ChurchwardPipeRenderer;
import frenk.eypipes.client.renderer.BentPipeRenderer;
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
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.function.Supplier;

/**
 * Client-side initialization and event handling for EyPipes mod.
 * Uses NeoForge event subscribers instead of Fabric's ClientModInitializer.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
@EventBusSubscriber(modid = EyPipes.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EyPipesClient {

    /**
     * Client setup event - runs after registries are complete.
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Set crop block to use cutout render layer (for transparency)
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ERBAPIPA_CROP.get(), RenderType.cutout());

            // Register Curios renderers
            CuriosRendererRegistry.register(ModItems.PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.CIGAR.get(), CigarCuriosRenderer::new);

            // Register Curios renderers for pipe variants
            CuriosRendererRegistry.register(ModItems.WOODEN_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.CLAY_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.CORN_COB_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.MEERSCHAUM_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.BRIAR_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.CHERRY_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.CALABASH_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.CHURCHWARD_PIPE.get(), PipeCuriosRenderer::new);
            CuriosRendererRegistry.register(ModItems.BENT_PIPE.get(), PipeCuriosRenderer::new);
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

        EyPipes.LOGGER.debug("Registered EyPipes block entity renderers (2 total)");
    }

    /**
     * Register client extensions for GeckoLib item renderers.
     */
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        // Register GeckoLib renderers for pipe and cigar
        event.registerItem(new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new PipeItemRenderer();
                }
                return renderer;
            }
        }, ModItems.PIPE.get());

        event.registerItem(new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new CigarItemRenderer();
                }
                return renderer;
            }
        }, ModItems.CIGAR.get());

        // Register 9 pipe variant renderers
        event.registerItem(createPipeExtension(WoodenPipeRenderer::new), ModItems.WOODEN_PIPE.get());
        event.registerItem(createPipeExtension(ClayPipeRenderer::new), ModItems.CLAY_PIPE.get());
        event.registerItem(createPipeExtension(CornCobPipeRenderer::new), ModItems.CORN_COB_PIPE.get());
        event.registerItem(createPipeExtension(MeerschaumPipeRenderer::new), ModItems.MEERSCHAUM_PIPE.get());
        event.registerItem(createPipeExtension(BriarPipeRenderer::new), ModItems.BRIAR_PIPE.get());
        event.registerItem(createPipeExtension(CherryPipeRenderer::new), ModItems.CHERRY_PIPE.get());
        event.registerItem(createPipeExtension(CalabashPipeRenderer::new), ModItems.CALABASH_PIPE.get());
        event.registerItem(createPipeExtension(ChurchwardPipeRenderer::new), ModItems.CHURCHWARD_PIPE.get());
        event.registerItem(createPipeExtension(BentPipeRenderer::new), ModItems.BENT_PIPE.get());

        EyPipes.LOGGER.debug("Registered EyPipes GeckoLib item renderers (12 total)");
    }

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

    /**
     * Helper method to create IClientItemExtensions for pipe variants.
     */
    private static IClientItemExtensions createPipeExtension(Supplier<GeoItemRenderer<?>> rendererSupplier) {
        return new IClientItemExtensions() {
            private GeoItemRenderer<?> renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = rendererSupplier.get();
                }
                return renderer;
            }
        };
    }
}
