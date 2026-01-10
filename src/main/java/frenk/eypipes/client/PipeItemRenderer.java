package frenk.eypipes.client;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class PipeItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final ThreadLocal<Boolean> RENDERING = ThreadLocal.withInitial(() -> false);
    
    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Prevent infinite recursion
        if (RENDERING.get()) {
            return;
        }
        RENDERING.set(true);
        
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            
            matrices.push();
        
        
            // Get the model manager and load the pipe model directly
            var modelManager = client.getBakedModelManager();
            var pipeModelId = new net.minecraft.client.util.ModelIdentifier("eypipes:pipe#inventory");
            var model = modelManager.getModel(pipeModelId);
            
            // Apply model transformations based on the rendering mode
            var transformation = model.getTransformation().getTransformation(mode);
            
            // Apply offset compensation before model transformation to counteract unwanted model offsets
            
            transformation.apply(false, matrices);
            
            // Use the appropriate render layer for items
            var vertexConsumer = vertexConsumers.getBuffer(net.minecraft.client.render.RenderLayer.getCutout());
            
            // Manually render the model quads
            var random = net.minecraft.util.math.random.Random.create();
            
            // Render quads for each direction
            for (net.minecraft.util.math.Direction direction : net.minecraft.util.math.Direction.values()) {
                random.setSeed(42L);
                var quads = model.getQuads(null, direction, random);
                for (var quad : quads) {
                    vertexConsumer.quad(matrices.peek(), quad, 1.0f, 1.0f, 1.0f, light, overlay);
                }
            }
            
            // Render quads without specific direction
            random.setSeed(42L);
            var quads = model.getQuads(null, null, random);
            for (var quad : quads) {
                vertexConsumer.quad(matrices.peek(), quad, 1.0f, 1.0f, 1.0f, light, overlay);
            }
            
            matrices.pop();
        } finally {
            RENDERING.set(false);
        }
    }
}