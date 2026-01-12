package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.block.CuttingBoardBlock;
import frenk.eypipes.block.entity.CuttingBoardBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renderer for CuttingBoardBlockEntity.
 * Renders the stored herb item lying flat on top of the cutting table.
 * When cut, the item appears flattened/stuck.
 */
public class CuttingBoardRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {

    private final ItemRenderer itemRenderer;

    public CuttingBoardRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(CuttingBoardBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack storedItem = blockEntity.getStoredItem();
        if (storedItem.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        // Get block facing direction
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(CuttingBoardBlock.FACING);

        // Position the item on top of the table (14 pixels high = 0.875)
        poseStack.translate(0.5, 0.9, 0.5);

        // Rotate based on block facing
        float rotation = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> 270f;
            default -> 0f; // NORTH
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // Lay the item flat (rotate 90 degrees on X axis)
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        // Normal scale
        poseStack.scale(0.6f, 0.6f, 0.6f);

        // Render the item
        itemRenderer.renderStatic(storedItem, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                poseStack, buffer, blockEntity.getLevel(), 0);

        poseStack.popPose();
    }
}
