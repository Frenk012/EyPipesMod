package frenk.eypipes;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import frenk.eypipes.block.EyPipesBlocks;
import frenk.eypipes.client.PipeTrinketRenderer;
import frenk.eypipes.client.PipeItemRenderer;
import frenk.eypipes.command.ClientReloadConfigCommand;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.item.EyPipesItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import frenk.eypipes.particles.EyPipesParticleTypes;
import frenk.eypipes.particles.custom.RingOfSmokeParticle;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class EyPipesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Load configuration on client side for renderers
        EyPipesConfig.loadConfig();
        
        BlockRenderLayerMap.INSTANCE.putBlock(EyPipesBlocks.ERBAPIPA_CROP, RenderLayer.getCutout());
        
        // Register the Block Entity Renderer for the drying rack
        BlockEntityRendererRegistry.register(PipesEntities.DRYING_RACK_ENTITY, DryingRackErbBlockEntityRenderer::new);
        
        // Register trinket renderer for the pipe
        TrinketRendererRegistry.registerRenderer(EyPipesItems.PIPE, new PipeTrinketRenderer());
        
        // Register custom item renderer for pipe animation
        BuiltinItemRendererRegistry.INSTANCE.register(EyPipesItems.PIPE, new PipeItemRenderer());
        
        // Register particle textures to the texture atlas
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier("eypipes", "big_smoke_ring_0"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_1"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_2"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_3"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_4"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_5"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_6"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_7"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_8"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_9"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_10"));
            registry.register(new Identifier("eypipes", "big_smoke_ring_11"));
        }));
        
        // Register particle factory for ring of smoke
        ParticleFactoryRegistry.getInstance().register(EyPipesParticleTypes.RING_OF_SMOKE, RingOfSmokeParticle.Factory::new);
        
        // Register client-side commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            ClientReloadConfigCommand.register(dispatcher);
        });
    }
}