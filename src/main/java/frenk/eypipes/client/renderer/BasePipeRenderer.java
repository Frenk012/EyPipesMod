package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.item.PipeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Base renderer for all pipe variants.
 * Handles custom first-person positioning during use.
 * Variant renderers extend this and provide their specific model.
 */
public abstract class BasePipeRenderer extends GeoItemRenderer<PipeItem> {

    // Shared static state for particle system (same for all pipe variants)
    private static Vec3 lastLocatorWorldPos = Vec3.ZERO;
    private static boolean isSmokingFirstPerson = false;

    private static final float SMOKING_TRANSLATE_X = 0.0f;
    private static final float SMOKING_TRANSLATE_Y = 0.1f;
    private static final float SMOKING_TRANSLATE_Z = 0.4f;
    private static final float SMOKING_ROTATE_X = 10.0f;
    private static final float TRANSITION_TICKS = 10.0f;

    public BasePipeRenderer(GeoModel<PipeItem> model) {
        super(model);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {

        boolean isFirstPerson = transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                                transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        Player player = Minecraft.getInstance().player;
        boolean isSmoking = player != null && player.isUsingItem() && player.getUseItem() == stack;

        isSmokingFirstPerson = isFirstPerson && isSmoking;

        if (isFirstPerson && isSmoking) {
            poseStack.pushPose();

            int useTicks = player.getTicksUsingItem();
            float progress = Math.min(useTicks / TRANSITION_TICKS, 1.0f);

            float translateX = SMOKING_TRANSLATE_X * progress;
            float translateY = SMOKING_TRANSLATE_Y * progress;
            float translateZ = SMOKING_TRANSLATE_Z * progress;
            float rotateX = SMOKING_ROTATE_X * progress;

            poseStack.translate(translateX, translateY, translateZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(rotateX));

            if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                poseStack.scale(-1.0f, 1.0f, 1.0f);
            }

            updateLocatorPosition(player);

            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        if (!isSmokingFirstPerson) {
            lastLocatorWorldPos = Vec3.ZERO;
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void updateLocatorPosition(Player player) {
        if (player == null) return;

        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 lookVec = player.getViewVector(1.0f);

        Vec3 flatLook = new Vec3(lookVec.x, 0, lookVec.z);
        if (flatLook.lengthSqr() < 0.0001) {
            float yawRad = (float) Math.toRadians(player.getYRot());
            flatLook = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        } else {
            flatLook = flatLook.normalize();
        }

        Vec3 upVec = new Vec3(0, 1, 0);
        Vec3 rightVec = flatLook.cross(upVec);

        float forwardOffset = 0.5f;
        float downOffset = -0.15f;
        float rightOffset = 0.4f;

        lastLocatorWorldPos = eyePos
                .add(flatLook.scale(forwardOffset))
                .add(upVec.scale(downOffset))
                .add(rightVec.scale(rightOffset));
    }

    public static Vec3 getLastLocatorWorldPos() {
        return lastLocatorWorldPos;
    }

    public static boolean isCurrentlySmokingFirstPerson() {
        return isSmokingFirstPerson;
    }
}
