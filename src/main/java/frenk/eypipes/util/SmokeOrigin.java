package frenk.eypipes.util;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Third-person smoke origin anchored to the hand holding the pipe or cigar.
 * Reproduces the HumanoidModel arm transform (including the smoking pose from
 * BipedModelMixin) so the smoke follows the arm instead of the head. Pure math,
 * so it gives the same result on the server and on the client.
 */
public final class SmokeOrigin {

    // Keep in sync with BipedModelMixin
    private static final float SMOKING_START_DELAY = 5.0f;
    private static final float SMOKING_ANIMATION_DURATION = 20.0f;
    private static final float SMOKING_PITCH = -1.5F;
    private static final float SMOKING_YAW = -0.5F;

    // Vanilla "holding an item" arm pitch, used for the arm the mixin does not animate
    private static final float HOLDING_PITCH = -(float) Math.PI / 10.0F;

    // Where ItemInHandLayer places the held item, in arm space (blocks)
    private static final float HAND_X = 1.0f / 16.0f;
    private static final float HAND_Y = 0.625f;
    private static final float HAND_Z = -0.125f;

    // PlayerRenderer scale and the model's vertical origin
    private static final float MODEL_SCALE = 0.9375f;
    private static final float MODEL_ORIGIN_Y = 1.501f;

    private SmokeOrigin() {}

    public static Vec3 handPosition(LivingEntity entity, float partialTick) {
        InteractionHand hand = entity.isUsingItem() ? entity.getUsedItemHand() : InteractionHand.MAIN_HAND;
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();
        boolean right = arm == HumanoidArm.RIGHT;

        // BipedModelMixin poses the right arm whenever the main-hand item is being smoked
        boolean animated = right && hand == InteractionHand.MAIN_HAND;
        float xRot = HOLDING_PITCH;
        float yRot = 0.0f;
        if (animated) {
            float progress = Mth.clamp((entity.getTicksUsingItem() + partialTick - SMOKING_START_DELAY)
                    / SMOKING_ANIMATION_DURATION, 0.0f, 1.0f);
            xRot = progress * SMOKING_PITCH;
            yRot = progress * SMOKING_YAW;
        }

        boolean crouching = entity.isCrouching();
        float side = right ? -1.0f : 1.0f;

        // Hand point in model space (pixels / 16): ModelPart applies pivot, then Z*Y*X rotation
        Vector3f point = new Vector3f(side * HAND_X, HAND_Y, HAND_Z)
                .rotateX(xRot)
                .rotateY(yRot)
                .add(side * 5.0f / 16.0f, (crouching ? 5.2f : 2.0f) / 16.0f, 0.0f);

        // LivingEntityRenderer: rotate Y by (180 - bodyYaw), scale (-1, -1, 1), entity scale, translate -1.501
        point.y -= MODEL_ORIGIN_Y;
        point.mul(-MODEL_SCALE, -MODEL_SCALE, MODEL_SCALE);
        float bodyYaw = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
        point.rotateY((float) Math.toRadians(180.0f - bodyYaw));

        Vec3 feet = entity.getPosition(partialTick);
        double crouchOffset = crouching ? -0.125 : 0.0;
        return feet.add(point.x, point.y + crouchOffset, point.z);
    }
}
