package frenk.eypipes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.client.model.HumanoidModel;

/**
 * Where a pipe or cigar sits on a player wearing it in an accessory slot. Shared by the Curios
 * (Forge, NeoForge) and Trinkets (Fabric) renderers, which differ only in how they are called.
 * Slot ids are Curios' and Trinkets' slot names; both mods use the same ones for EyPipes' slots.
 */
public final class WornItemPose {
    private WornItemPose() {
    }

    /** Position the pipe on the wearer according to which slot holds it. */
    public static void pipe(PoseStack poseStack, HumanoidModel<?> humanoidModel, String slotId) {
        if ("chest".equals(slotId) || "pipe_chest".equals(slotId)) {
            // Transform to right hand position for chest slot
            humanoidModel.rightArm.translateAndRotate(poseStack);

            float offsetX = (float) (double) EyPipesConfig.CLIENT.particleOffsetThirdViewX.get();
            float offsetY = (float) (double) EyPipesConfig.CLIENT.particleOffsetThirdViewY.get();
            float offsetZ = (float) (double) EyPipesConfig.CLIENT.particleOffsetThirdViewZ.get();

            poseStack.translate(offsetX, offsetY, offsetZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));  // Rotate to hold properly
            poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // Rotate to hold properly
            poseStack.mulPose(Axis.YP.rotationDegrees(330)); // Adjust orientation
            poseStack.scale(0.9f, 0.9f, 0.9f);
        } else {
            // Default head position for other slots (like pipe_head)
            humanoidModel.head.translateAndRotate(poseStack);

            // Head slot placement
            poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // Rotation: Z-axis rotation
            poseStack.mulPose(Axis.YP.rotationDegrees(350)); // Rotation: Y-axis tilt
            poseStack.scale(0.9f, 0.9f, 0.9f);
        }
    }

    /** Put the cigar in the wearer's mouth. Only the head slot positions it. */
    public static void cigar(PoseStack poseStack, HumanoidModel<?> humanoidModel, String slotId) {
        if ("head".equals(slotId) || "pipe_head".equals(slotId)) {
            humanoidModel.head.translateAndRotate(poseStack);

            // Apply cigar transformations (converted from model units to world units)
            poseStack.translate(0.0f, 0.6f / 16.0f, -4.75f / 16.0f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.scale(0.7f, 0.7f, 0.7f);
        }
    }
}
