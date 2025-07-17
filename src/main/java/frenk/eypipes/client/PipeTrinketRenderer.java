package frenk.eypipes.client;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class PipeTrinketRenderer implements TrinketRenderer {
    
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (contextModel instanceof PlayerEntityModel<?> playerModel) {
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            
            matrices.push();
            
            // Check the slot type to determine rendering position
            String slotName = slotReference.inventory().getSlotType().getName();
            
            if ("pipe_chest".equals(slotName)) {
                // Transform to right hand position for chest slot
                playerModel.rightArm.rotate(matrices);
                
                matrices.translate(EyPipesConfig.PARTICLE_OFFSET_THIRDVIEW_X, EyPipesConfig.PARTICLE_OFFSET_THIRDVIEW_Y, EyPipesConfig.PARTICLE_OFFSET_THIRDVIEW_Z);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(25)); // Rotate to hold properly
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180)); // Rotate to hold properly
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(340)); // Adjust orientation
                matrices.scale(0.9f, 0.9f, 0.9f); // Scale down for hand
            } else {
                // Default head position for other slots (like pipe_head)
                playerModel.head.rotate(matrices);
                
                // Apply custom transformations for head trinket slot positioning
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180)); // Rotation: Z-axis rotation
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(350)); // Rotation: Y-axis tilt
                matrices.scale(0.9f, 0.9f, 0.9f); // Scale: uniform scaling
            }
            
            // Render the item
            itemRenderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, 0, matrices, vertexConsumers, 0);
            
            matrices.pop();
        }
    }
}