package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.block.CuttingBoardBlock;
import frenk.eypipes.block.entity.CuttingBoardBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
*///?}

/**
 * Renderer for CuttingBoardBlockEntity.
 * Renders the stored herb item lying flat on top of the cutting table.
 *
 * <p>From 1.21.9 a block entity renderer reads the world during an extract phase and draws from
 * the captured state later, so the stored stack is resolved into an {@code ItemStackRenderState}
 * up front. The placement maths is shared by both paths.
 */
//? if <1.21.9 {
public class CuttingBoardRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {
//?} else
/*public class CuttingBoardRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity, CuttingBoardRenderer.State> {*/

    //? if >=1.21.9 {
    /*public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState item = new ItemStackRenderState();
        public BlockState boardState = Blocks.AIR.defaultBlockState();
        public boolean hasItem;
    }

    private final ItemModelResolver itemModelResolver;
    *///?}

    public CuttingBoardRenderer(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.9
        /*this.itemModelResolver = context.itemModelResolver();*/
    }

    /** Place the item flat on top of the board, turned to match the block. */
    private static void applyPose(PoseStack poseStack, BlockState state) {
        // Position the item on top of the table (14 pixels high = 0.875)
        poseStack.translate(0.5, 0.9, 0.5);

        Direction facing = state.getValue(CuttingBoardBlock.FACING);
        float rotation = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> 90f;
            case EAST -> 270f;
            default -> 0f; // NORTH
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // Lay the item flat (rotate 90 degrees on X axis)
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        poseStack.scale(0.6f, 0.6f, 0.6f);
    }

    //? if <1.21.9 {
    @Override
    public void render(CuttingBoardBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack storedItem = blockEntity.getStoredItem();
        if (storedItem.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        applyPose(poseStack, blockEntity.getBlockState());
        ItemStackRenderHelper.render(storedItem, ItemDisplayContext.FIXED, poseStack, buffer,
                blockEntity.getLevel(), packedLight, packedOverlay);
        poseStack.popPose();
    }
    //?} else {
    /*@Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CuttingBoardBlockEntity blockEntity, State renderState, float partialTick,
            Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);

        ItemStack storedItem = blockEntity.getStoredItem();
        renderState.hasItem = !storedItem.isEmpty();
        renderState.boardState = blockEntity.getBlockState();

        if (renderState.hasItem) {
            this.itemModelResolver.updateForTopItem(renderState.item, storedItem,
                    ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(State renderState, PoseStack poseStack, SubmitNodeCollector collector,
            CameraRenderState cameraRenderState) {
        if (!renderState.hasItem) {
            return;
        }

        poseStack.pushPose();
        applyPose(poseStack, renderState.boardState);
        renderState.item.submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
    *///?}
}
