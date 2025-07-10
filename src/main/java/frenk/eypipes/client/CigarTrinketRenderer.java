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

public class CigarTrinketRenderer implements TrinketRenderer {
    
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (contextModel instanceof PlayerEntityModel<?> playerModel) {
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            
            matrices.push();
            
            // Check the slot type to determine rendering position
            String slotName = slotReference.inventory().getSlotType().getName();
            
            if ("pipe_head".equals(slotName)) {
                // Transform to head position for head slot
                playerModel.head.rotate(matrices);
                
                // Apply cigar.json head transformations
                
                matrices.translate(0.0f, 0.6f / 16.0f, -4.75f / 16.0f); // Convert from model units to world units
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180));
                matrices.scale(0.7f, 0.7f, 0.7f);
            }
            
            // Render the item
            itemRenderer.renderItem(stack, ModelTransformation.Mode.FIXED, light, 0, matrices, vertexConsumers, 0);
            
            matrices.pop();
        }
    }
}