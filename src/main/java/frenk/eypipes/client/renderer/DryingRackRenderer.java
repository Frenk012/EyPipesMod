package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.block.DryingRackBlock;
import frenk.eypipes.block.entity.DryingRackBlockEntity;
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
 * Block entity renderer for the drying rack.
 * Renders items on the rack with proper rotation based on block facing.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
//? if <1.21.9 {
public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {
//?} else
/*public class DryingRackRenderer implements BlockEntityRenderer<DryingRackBlockEntity, DryingRackRenderer.State> {*/

    //? if >=1.21.9 {
    /*public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] items = new ItemStackRenderState[3];
        public Direction facing = Direction.NORTH;

        public State() {
            for (int i = 0; i < 3; i++) {
                items[i] = new ItemStackRenderState();
            }
        }
    }

    private final ItemModelResolver itemModelResolver;
    *///?}

    public DryingRackRenderer(BlockEntityRendererProvider.Context context) {
        //? if >=1.21.9
        /*this.itemModelResolver = context.itemModelResolver();*/
    }

    /** Base slot positions relative to a north-facing rack. */
    private static final double[][] SLOT_POSITIONS = {
            {0.25, 0.50, 0.90},  // Left position (front)
            {0.50, 0.50, 0.78},  // Center position (middle)
            {0.75, 0.50, 0.65}   // Right position (back)
    };

    /** Place and orient one slot. Shared by both render paths. */
    private void applySlotPose(PoseStack poseStack, int slot, Direction facing) {
        double[] basePos = SLOT_POSITIONS[slot];
        double[] transformedPos = transformPosition(basePos[0], basePos[1], basePos[2], facing);

        poseStack.translate(transformedPos[0], transformedPos[1], transformedPos[2]);
        poseStack.scale(1.1f, 1.1f, 1.1f); // Scale items to fit
        poseStack.mulPose(Axis.YP.rotationDegrees(getItemRotation(facing)));
    }

    //? if <1.21.9 {
    @Override
    public void render(DryingRackBlockEntity entity, float partialTick, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack[] items = entity.getAllItems();
        Direction facing = entity.getBlockState().getValue(DryingRackBlock.FACING);

        for (int i = 0; i < SLOT_POSITIONS.length; i++) {
            if (items[i].isEmpty()) continue;

            poseStack.pushPose();
            applySlotPose(poseStack, i, facing);
            ItemStackRenderHelper.render(items[i], ItemDisplayContext.GROUND, poseStack, bufferSource,
                    entity.getLevel(), packedLight, packedOverlay);
            poseStack.popPose();
        }
    }
    //?} else {
    /*@Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(DryingRackBlockEntity entity, State renderState, float partialTick,
            Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, renderState, partialTick, cameraPosition, breakProgress);

        renderState.facing = entity.getBlockState().getValue(DryingRackBlock.FACING);
        ItemStack[] items = entity.getAllItems();

        for (int i = 0; i < renderState.items.length; i++) {
            this.itemModelResolver.updateForTopItem(renderState.items[i], items[i],
                    ItemDisplayContext.GROUND, entity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(State renderState, PoseStack poseStack, SubmitNodeCollector collector,
            CameraRenderState cameraRenderState) {
        for (int i = 0; i < renderState.items.length; i++) {
            if (renderState.items[i].isEmpty()) continue;

            poseStack.pushPose();
            applySlotPose(poseStack, i, renderState.facing);
            renderState.items[i].submit(poseStack, collector, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
    *///?}

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
