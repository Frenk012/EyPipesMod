package frenk.eypipes.fabric;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import frenk.eypipes.EyPipes;
import frenk.eypipes.client.EyPipesClientCommon;
import frenk.eypipes.client.renderer.CuttingBoardRenderer;
import frenk.eypipes.client.renderer.DryingRackRenderer;
import frenk.eypipes.client.renderer.PipeRackRenderer;
import frenk.eypipes.client.trinkets.CigarTrinketRenderer;
import frenk.eypipes.client.trinkets.PipeTrinketRenderer;
import frenk.eypipes.particle.EmberParticle;
import frenk.eypipes.particle.RingOfSmokeParticle;
import frenk.eypipes.particle.SmokeStreamParticle;
import frenk.eypipes.particle.SparkParticle;
import frenk.eypipes.particle.SpiralSmokeParticle;
import frenk.eypipes.particle.SteamParticle;
import frenk.eypipes.platform.RegistryEntry;
import frenk.eypipes.registries.ModBlockEntities;
import frenk.eypipes.registries.ModBlocks;
import frenk.eypipes.registries.ModItems;
import frenk.eypipes.registries.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.Item;

/**
 * Fabric client entry point: the same registrations the (Neo)Forge builds make from their
 * client events in {@code EyPipesClient} and {@code EyPipesClientEvents}.
 */
public class EyPipesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ERBAPIPA_CROP.get(), RenderType.cutout());

        BlockEntityRenderers.register(ModBlockEntities.DRYING_RACK.get(), DryingRackRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.PIPE_RACK.get(), PipeRackRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CUTTING_BOARD.get(), CuttingBoardRenderer::new);

        ParticleFactoryRegistry particles = ParticleFactoryRegistry.getInstance();
        particles.register(ModParticles.RING_OF_SMOKE.get(), RingOfSmokeParticle.Provider::new);
        particles.register(ModParticles.EMBER.get(), EmberParticle.Provider::new);
        particles.register(ModParticles.SPIRAL_SMOKE.get(), SpiralSmokeParticle.Provider::new);
        particles.register(ModParticles.SPARK.get(), SparkParticle.Provider::new);
        particles.register(ModParticles.SMOKE_STREAM.get(), SmokeStreamParticle.Provider::new);
        particles.register(ModParticles.STEAM.get(), SteamParticle.Provider::new);

        // Trinkets slot renderers: every pipe variant shares one, the cigar has its own
        for (RegistryEntry<Item> pipe : ModItems.PIPES) {
            TrinketRendererRegistry.registerRenderer(pipe.get(), new PipeTrinketRenderer());
        }
        TrinketRendererRegistry.registerRenderer(ModItems.CIGAR.get(), new CigarTrinketRenderer());

        ClientTickEvents.END_CLIENT_TICK.register(client -> EyPipesClientCommon.onClientTickEnd());
        //? if >=1.20.5 {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) ->
                EyPipesClientCommon.appendQualityTooltip(stack, lines));
        //?} else {
        /*ItemTooltipCallback.EVENT.register((stack, flag, lines) ->
                EyPipesClientCommon.appendQualityTooltip(stack, lines));
        *///?}

        EyPipes.LOGGER.info("EyPipes client setup complete");
    }
}
