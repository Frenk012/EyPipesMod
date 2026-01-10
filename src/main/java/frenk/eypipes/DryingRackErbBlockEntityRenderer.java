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
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;

public class DryingRackErbBlockEntityRenderer implements BlockEntityRenderer<DryingRackErbBlockEntity> {
    
    public DryingRackErbBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        // Constructor required by BlockEntityRendererFactory
    }
    
    @Override
    public void render(DryingRackErbBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack[] items = entity.getAllItems();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        
        // Get the block's facing direction
        BlockState blockState = entity.getCachedState();
        Direction facing = blockState.get(DryingRackErbBlock.FACING);
        
        // Define base positions for the 3 items on the drying rack (relative to NORTH facing)
        // Items are arranged left to right with consistent spacing and depth ordering
        double[][] basePositions = {
            {0.25, 0.50, 0.90},  // Left position (front)
            {0.50, 0.50, 0.78},  // Center position (middle)
            {0.75, 0.50, 0.65}   // Right position (back)
        };

        for (int i = 0; i < 3; i++) {
            ItemStack item = items[i];
            
            if (!item.isEmpty()) {
                matrices.push();
                
                // Get the base position
                double[] basePos = basePositions[i];
                double x = basePos[0];
                double y = basePos[1];
                double z = basePos[2];
                
                // Transform position based on block facing direction
                double[] transformedPos = transformPosition(x, y, z, facing);
                
                // Position the item on the drying rack
                matrices.translate(transformedPos[0], transformedPos[1], transformedPos[2]);
                matrices.scale(1.1f, 1.1f, 1.1f); // Make the items smaller to fit 3
                
                // Rotate the item based on block orientation
                float itemRotation = getItemRotation(facing);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(itemRotation));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(0));
                
                // Render the item
                itemRenderer.renderItem(item, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);
                
                matrices.pop();
            }
        }
    }
    
    /**
     * Transforms the position coordinates based on the block's facing direction
     * This method rotates the entire arrangement around the block center (0.5, 0.5, 0.5)
     * while preserving relative distances and depth ordering
     */
    private double[] transformPosition(double x, double y, double z, Direction facing) {
        // Translate to origin (center of block)
        double centerX = x - 0.5;
        double centerZ = z - 0.5;
        
        double newX, newZ;
        
        switch (facing) {
            case NORTH:
                // No rotation needed
                newX = centerX;
                newZ = centerZ;
                break;
            case SOUTH:
                // 180 degree rotation
                newX = -centerX;
                newZ = -centerZ;
                break;
            case EAST:
                // 90 degree clockwise rotation
                newX = -centerZ;
                newZ = centerX;
                break;
            case WEST:
                // 90 degree counter-clockwise rotation
                newX = centerZ;
                newZ = -centerX;
                break;
            default:
                newX = centerX;
                newZ = centerZ;
                break;
        }
        
        // Translate back from origin
        return new double[]{newX + 0.5, y, newZ + 0.5};
    }
    
    /**
     * Gets the item rotation based on the block's facing direction
     */
    private float getItemRotation(Direction facing) {
        switch (facing) {
            case NORTH:
                return 0f;
            case SOUTH:
                return 180f;
            case EAST:
                return 270f;
            case WEST:
                return 90f;
            default:
                return 0f;
        }
    }
}