package frenk.eypipes.client;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
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
            
            // Transform to head position
            playerModel.head.rotate(matrices);
            
            // Apply custom transformations for trinket slot positioning
            //matrices.translate(1, 1, 1); // Position: X, Y, Z offset from head
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180)); // Rotation: Z-axis rotation
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(350)); // Rotation: X-axis tilt
            matrices.scale(0.78f, 0.78f, 0.78f); // Scale: uniform scaling
            
            // Render the item
            itemRenderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, 0, matrices, vertexConsumers, 0);
            
            matrices.pop();
        }
    }
}