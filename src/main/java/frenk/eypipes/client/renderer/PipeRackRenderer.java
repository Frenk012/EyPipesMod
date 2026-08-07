package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.block.PipeRackBlock;
import frenk.eypipes.block.entity.PipeRackBlockEntity;
//? if <1.21.9 {
import frenk.eypipes.compat.ItemStackRenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
//?} else {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
*///?}
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
//? if <1.21.9 {
public class PipeRackRenderer implements BlockEntityRenderer<PipeRackBlockEntity> {
//?} else
/*public class PipeRackRenderer implements BlockEntityRenderer<PipeRackBlockEntity, PipeRackRenderer.State> {*/

    //? if >=1.21.9 {
    /*public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[4];
        public Direction facing = Direction.NORTH;

        public State() {
            for (int i = 0; i < 4; i++) {
                items[i] = new ItemStackRenderState();
            }
        }
    }

    private final ItemModelResolver itemModelResolver;
    *///?}

    public PipeRackRenderer(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.9
        /*this.itemModelResolver = context.itemModelResolver();*/
    }

    /** Place, orient and scale one pipe slot. Shared by both render paths. */
    private void applySlotPose(PoseStack poseStack, int slot, Direction facing) {
        positionForFacing(poseStack, facing, getSlotOffset(slot));
        poseStack.scale(0.6f, 0.6f, 0.6f);
    }

    //? if <1.21.9 {
    @Override
    public void render(PipeRackBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                      MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack[] items = blockEntity.getAllItems();
        Direction facing = blockEntity.getBlockState().getValue(PipeRackBlock.FACING);

        for (int slot = 0; slot < items.length; slot++) {
            if (items[slot].isEmpty()) continue;

            poseStack.pushPose();
            applySlotPose(poseStack, slot, facing);
            ItemStackRenderHelper.render(items[slot], ItemDisplayContext.FIXED, poseStack, bufferSource,
                    blockEntity.getLevel(), packedLight, packedOverlay);
            poseStack.popPose();
        }
    }
    //?} else {
    /*@Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PipeRackBlockEntity blockEntity, State renderState, float partialTick,
            Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.facing = blockEntity.getBlockState().getValue(PipeRackBlock.FACING);
        ItemStack[] items = blockEntity.getAllItems();

        for (int slot = 0; slot < renderState.items.length; slot++) {
            this.itemModelResolver.updateForTopItem(renderState.items[slot], items[slot],
                    ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(State renderState, PoseStack poseStack, SubmitNodeCollector collector,
            CameraRenderState cameraRenderState) {
        for (int slot = 0; slot < renderState.items.length; slot++) {
            if (renderState.items[slot].isEmpty()) continue;

            poseStack.pushPose();
            applySlotPose(poseStack, slot, renderState.facing);
            renderState.items[slot].submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
    *///?}

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
