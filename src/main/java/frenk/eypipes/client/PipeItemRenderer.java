package frenk.eypipes.client;

import frenk.eypipes.item.PipeItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

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
            PlayerEntity player = client.player;
            
            matrices.push();
        
            // Check if the item is being used (right-clicked) and apply animation
        // Only apply animation in first-person contexts, not in GUI/inventory
        if (mode != ModelTransformation.Mode.GUI && player != null && player.isUsingItem() && player.getActiveItem() == stack && stack.getItem() instanceof PipeItem pipeItem) {
            if (pipeItem.isSmoking()) {
                // Calculate animation progress based on use time
                int useTime = player.getItemUseTime();
                // Only start animation after a brief delay (5 ticks) to avoid immediate movement when picking up
                if (useTime > 5) {
                    float animationProgress = Math.min((useTime - 5) / 20.0f, 1.0f); // 20 ticks = 1 second for full animation
                    
                    // Apply first person animation (since this is typically for held items)
                    applyFirstPersonAnimation(matrices, animationProgress);
                }
            }
        }
        
            // Get the model manager and load the pipe model directly
            var modelManager = client.getBakedModelManager();
            var pipeModelId = new net.minecraft.client.util.ModelIdentifier("eypipes:pipe#inventory");
            var model = modelManager.getModel(pipeModelId);
            
            // Apply model transformations based on the rendering mode
            var transformation = model.getTransformation().getTransformation(mode);
            
            // Apply offset compensation before model transformation to counteract unwanted model offsets
            applyOffsetCompensation(matrices, mode);
            
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
    
    private void applyOffsetCompensation(MatrixStack matrices, ModelTransformation.Mode mode) {
        // Compensate for unwanted offsets in the model file
        // These values counteract the translation values in pipe.json's display section
        switch (mode) {
            case HEAD -> {
                matrices.translate(1.0f, 0.0f, 0.0f);
            }
            case FIXED -> {
                matrices.translate(-0.5f, 0.0f, 0.0f);
            }
            case THIRD_PERSON_LEFT_HAND -> {
                matrices.translate(0.25f, 0.0f, 0.0f);
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                matrices.translate(0.7f, 0.2f, 0.0f);
            }
            case GUI, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, NONE, GROUND -> {
                // These modes don't need compensation as they don't have problematic offsets in the model
            }
        }
    }
    
    private void applyFirstPersonAnimation(MatrixStack matrices, float progress) {
        // Smooth animation curve
        float smoothProgress = MathHelper.sin(progress * (float) Math.PI * 0.5f);
        
        // Move the pipe from standard hand position to face
        // Standard position adjustments
        matrices.translate(0, 0.1f * smoothProgress, 0.25f * smoothProgress);
        
        // Rotate to bring the tip towards the face
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(15.0f * smoothProgress));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(10.0f * smoothProgress));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-5.0f * smoothProgress));
    }
    

}