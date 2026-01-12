package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.block.PipeRackBlock;
import frenk.eypipes.block.entity.PipeRackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renderer for the Pipe Rack block entity.
 * Renders pipes placed in the rack slots.
 */
public class PipeRackRenderer implements BlockEntityRenderer<PipeRackBlockEntity> {

    public PipeRackRenderer(BlockEntityRendererProvider.Context context) {
        // Context available if needed for item renderer access
    }

    @Override
    public void render(PipeRackBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack[] items = blockEntity.getAllItems();
        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(PipeRackBlock.FACING);

        for (int slot = 0; slot < items.length; slot++) {
            ItemStack stack = items[slot];
            if (!stack.isEmpty()) {
                poseStack.pushPose();

                // Position based on facing direction and slot
                float slotOffset = getSlotOffset(slot);
                positionForFacing(poseStack, facing, slotOffset);

                // Scale down the pipe
                poseStack.scale(0.6f, 0.6f, 0.6f);

                // Render the item
                Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack,
                        ItemDisplayContext.FIXED,
                        packedLight,
                        packedOverlay,
                        poseStack,
                        bufferSource,
                        blockEntity.getLevel(),
                        0
                );

                poseStack.popPose();
            }
        }
    }

    private float getSlotOffset(int slot) {
        // 4 slots spread across the rack width
        // Slots: 0 -> 0.2, 1 -> 0.4, 2 -> 0.6, 3 -> 0.8
        return 0.2f + (slot * 0.2f);
    }

    private void positionForFacing(PoseStack poseStack, Direction facing, float slotOffset) {
        // Pipes are laid on their side, slightly tilted, resting on the rack
        // Each pipe has a small random-looking tilt based on slot position
        float tilt = 8f + (slotOffset * 5f); // Slight variation per slot

        switch (facing) {
            case NORTH -> {
                // Rack on north wall, pipes facing south
                poseStack.translate(slotOffset, 0.35, 0.75);
                // Lay pipe on its side (rotate around X)
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                // Rotate to face outward
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                // Slight tilt for natural look
                poseStack.mulPose(Axis.YP.rotationDegrees(tilt));
            }
            case SOUTH -> {
                // Rack on south wall, pipes facing north
                poseStack.translate(1.0f - slotOffset, 0.35, 0.25);
                // Lay pipe on its side
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                // Face outward
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                // Slight tilt
                poseStack.mulPose(Axis.YP.rotationDegrees(-tilt));
            }
            case EAST -> {
                // Rack on east wall, pipes facing west
                poseStack.translate(0.25, 0.35, slotOffset);
                // Lay pipe on its side
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                // Face outward
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                // Slight tilt
                poseStack.mulPose(Axis.XP.rotationDegrees(tilt));
            }
            case WEST -> {
                // Rack on west wall, pipes facing east
                poseStack.translate(0.75, 0.35, 1.0f - slotOffset);
                // Lay pipe on its side
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                // Face outward
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                // Slight tilt
                poseStack.mulPose(Axis.XP.rotationDegrees(-tilt));
            }
            default -> {
                poseStack.translate(slotOffset, 0.35, 0.75);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
            }
        }
    }
}
