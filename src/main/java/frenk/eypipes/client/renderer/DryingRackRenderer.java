package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.entity.DryingRackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity renderer for the drying rack.
 * Renders items on the rack with proper rotation based on block facing.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {

    public DryingRackRenderer(BlockEntityRendererProvider.Context context) {
        // Constructor required by BlockEntityRendererProvider
    }

    @Override
    public void render(DryingRackBlockEntity entity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack[] items = entity.getAllItems();

        // Get the block's facing direction
        BlockState blockState = entity.getBlockState();
        Direction facing = blockState.getValue(DryingRackBlock.FACING);

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
                poseStack.pushPose();

                // Get the base position
                double[] basePos = basePositions[i];
                double x = basePos[0];
                double y = basePos[1];
                double z = basePos[2];

                // Transform position based on block facing direction
                double[] transformedPos = transformPosition(x, y, z, facing);

                // Position the item on the drying rack
                poseStack.translate(transformedPos[0], transformedPos[1], transformedPos[2]);
                poseStack.scale(1.1f, 1.1f, 1.1f); // Scale items to fit

                // Rotate the item based on block orientation
                float itemRotation = getItemRotation(facing);
                poseStack.mulPose(Axis.YP.rotationDegrees(itemRotation));
                poseStack.mulPose(Axis.XP.rotationDegrees(0));

                // Render the item
                Minecraft.getInstance().getItemRenderer().renderStatic(
                        item,
                        ItemDisplayContext.GROUND,
                        packedLight,
                        packedOverlay,
                        poseStack,
                        bufferSource,
                        entity.getLevel(),
                        0
                );

                poseStack.popPose();
            }
        }
    }

    /**
     * Transforms the position coordinates based on the block's facing direction.
     * This method rotates the entire arrangement around the block center (0.5, 0.5, 0.5)
     * while preserving relative distances and depth ordering.
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
     * Gets the item rotation based on the block's facing direction.
     */
    private float getItemRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 0f;
            case SOUTH -> 180f;
            case EAST -> 270f;
            case WEST -> 90f;
            default -> 0f;
        };
    }
}
