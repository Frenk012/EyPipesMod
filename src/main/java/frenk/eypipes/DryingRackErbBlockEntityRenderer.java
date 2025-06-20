package frenk.eypipes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class DryingRackErbBlockEntityRenderer implements BlockEntityRenderer<DryingRackErbBlockEntity> {
    
    public DryingRackErbBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        // Constructor required by BlockEntityRendererFactory
    }
    
    @Override
    public void render(DryingRackErbBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack[] items = entity.getAllItems();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        
        // Define positions for the 3 items on the drying rack
        double[][] positions = {
            {0.90, 0.50, 0.5},  // Left position
            {0.78, 0.50, 0.5},  // Center position
            {0.65, 0.50, 0.5}   // Right position
        };
        
        for (int i = 0; i < 3; i++) {
            ItemStack item = items[i];
            
            if (!item.isEmpty()) {
                matrices.push();
                
                // Position the item on the drying rack
                matrices.translate(positions[i][0], positions[i][1], positions[i][2]);
                matrices.scale(1.1f, 1.1f, 1.1f); // Make the items smaller to fit 3
                
                // Rotate the item slightly for a more natural look
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0));
                
                // Render the item
                itemRenderer.renderItem(item, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);
                
                matrices.pop();
            }
        }
    }
}