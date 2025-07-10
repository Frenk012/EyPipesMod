package frenk.eypipes.client;

import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.item.CigarItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class CigarItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
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
        if (player != null && player.isUsingItem() && player.getActiveItem() == stack && stack.getItem() instanceof CigarItem cigarItem) {
            if (cigarItem.isSmoking()) {
                int useTime = player.getItemUseTime();
                if (useTime > 5) {
                    float animationProgress = Math.min((useTime - 5) / 20.0f, 1.0f);

                    boolean isFirstPerson = mode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND || mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND;
                    
                    if (isFirstPerson) {
                        applyFirstPersonAnimation(matrices, animationProgress);
                    } //else if (isThirdPerson) {
                      //  applyThirdPersonAnimation(matrices, animationProgress);
                    //}
                }
            }
        }
        
            // Get the model manager and load the cigar model directly
            var modelManager = client.getBakedModelManager();
            var cigarModelId = new net.minecraft.client.util.ModelIdentifier("eypipes:cigar#inventory");
            var model = modelManager.getModel(cigarModelId);
            
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
        // These values counteract the translation values in cigar.json's display section
        switch (mode) {
            case HEAD -> {
                matrices.translate(EyPipesConfig.OFFSET_HEAD_X, EyPipesConfig.OFFSET_HEAD_Y, EyPipesConfig.OFFSET_HEAD_Z);
            }
            case FIXED -> {
                matrices.translate(EyPipesConfig.OFFSET_FIXED_X, EyPipesConfig.OFFSET_FIXED_Y, EyPipesConfig.OFFSET_FIXED_Z);
            }
            case THIRD_PERSON_RIGHT_HAND -> {
                matrices.translate(EyPipesConfig.OFFSET_THIRD_PERSON_RIGHT_X, EyPipesConfig.OFFSET_THIRD_PERSON_RIGHT_Y, EyPipesConfig.OFFSET_THIRD_PERSON_RIGHT_Z);
            }
            case THIRD_PERSON_LEFT_HAND -> {
                matrices.translate(EyPipesConfig.OFFSET_THIRD_PERSON_LEFT_X, EyPipesConfig.OFFSET_THIRD_PERSON_LEFT_Y, EyPipesConfig.OFFSET_THIRD_PERSON_LEFT_Z);
            }
            case FIRST_PERSON_LEFT_HAND -> {
                matrices.translate(EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_X, EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_Y, EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_Z);
            }
            case FIRST_PERSON_RIGHT_HAND -> {
                matrices.translate(EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_X, EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_Y, EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_Z);
            }
            case GUI -> {
                matrices.translate(EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_X, EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_Y, EyPipesConfig.OFFSET_FIRST_PERSON_RIGHT_Z);
            }
            case NONE, GROUND -> {
                // These modes don't need compensation as they don't have problematic offsets in the model
            }
        }
    }

    private void applyFirstPersonAnimation(MatrixStack matrices, float progress) {
        // Smooth animation curve using configurable multiplier
        float smoothProgress = MathHelper.sin(progress * (float) Math.PI * (float) EyPipesConfig.FIRST_PERSON_CURVE_MULTIPLIER);
        
        // Move the cigar from standard hand position to face using configurable values
        matrices.translate(
            EyPipesConfig.FIRST_PERSON_X_TRANSLATION * smoothProgress,
            EyPipesConfig.FIRST_PERSON_Y_TRANSLATION * smoothProgress,
            EyPipesConfig.FIRST_PERSON_Z_TRANSLATION * smoothProgress
        );
        
        // Rotate to bring the tip towards the face (if enabled)
        if (EyPipesConfig.FIRST_PERSON_ENABLE_ROTATION) {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(EyPipesConfig.FIRST_PERSON_X_ROTATION * smoothProgress));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(EyPipesConfig.FIRST_PERSON_Y_ROTATION * smoothProgress));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(EyPipesConfig.FIRST_PERSON_Z_ROTATION * smoothProgress));
        }
    }
}