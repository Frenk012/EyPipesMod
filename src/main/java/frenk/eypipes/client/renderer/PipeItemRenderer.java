package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.client.model.PipeModel;
import frenk.eypipes.item.PipeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * GeckoLib 4 item renderer for the PipeItem.
 * Handles custom first-person positioning during use - brings pipe closer to face.
 * Also tracks locator position for particle spawning.
 */
public class PipeItemRenderer extends GeoItemRenderer<PipeItem> {

    // Store the last computed locator world position for particle spawning
    private static Vec3 lastLocatorWorldPos = Vec3.ZERO;
    private static boolean isSmokingFirstPerson = false;

    // Transform values for bringing pipe to face in first person
    // Note: In first-person item rendering, positive Z moves TOWARD camera
    private static final float SMOKING_TRANSLATE_X = 0.0f;   // No horizontal shift
    private static final float SMOKING_TRANSLATE_Y = 0.1f;   // Slight up (was too low)
    private static final float SMOKING_TRANSLATE_Z = 0.4f;   // TOWARD camera (positive Z)
    private static final float SMOKING_ROTATE_X = 10.0f;     // Tilt pipe slightly toward mouth

    // Animation transition speed (ticks to full transform)
    private static final float TRANSITION_TICKS = 10.0f;

    public PipeItemRenderer() {
        super(new PipeModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {

        boolean isFirstPerson = transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                                transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        Player player = Minecraft.getInstance().player;
        boolean isSmoking = player != null && player.isUsingItem() && player.getUseItem() == stack;

        // Update static state for particle system
        isSmokingFirstPerson = isFirstPerson && isSmoking;

        if (isFirstPerson && isSmoking) {
            poseStack.pushPose();

            // Calculate smooth animation progress based on use time
            int useTicks = player.getTicksUsingItem();
            float progress = Math.min(useTicks / TRANSITION_TICKS, 1.0f);

            // Interpolate transforms for smooth transition
            float translateX = SMOKING_TRANSLATE_X * progress;
            float translateY = SMOKING_TRANSLATE_Y * progress;
            float translateZ = SMOKING_TRANSLATE_Z * progress;
            float rotateX = SMOKING_ROTATE_X * progress;

            // Apply transforms: bring pipe closer to face
            poseStack.translate(translateX, translateY, translateZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(rotateX));

            // Mirror for left hand
            if (transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
                poseStack.scale(-1.0f, 1.0f, 1.0f);
            }

            // Compute locator world position for particle spawning
            // The locator is at [0, 7.5, 2] in model space (Blockbench units, 1 unit = 1/16 block)
            // After transforms, we need to estimate where it ends up relative to the player
            updateLocatorPosition(player, poseStack);

            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        // Reset locator position when not smoking in first person
        if (!isSmokingFirstPerson) {
            lastLocatorWorldPos = Vec3.ZERO;
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    /**
     * Compute the approximate world position of the pipe's bowl locator.
     * This is used by the particle system to spawn particles at the correct position.
     * The bowl (locator) should be visible in front of and slightly below the camera.
     */
    private void updateLocatorPosition(Player player, PoseStack poseStack) {
        if (player == null) return;

        // Get player's eye position and view direction
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 lookVec = player.getViewVector(1.0f);

        // Get horizontal look direction only (ignore pitch)
        Vec3 flatLook = new Vec3(lookVec.x, 0, lookVec.z);
        if (flatLook.lengthSqr() < 0.0001) {
            // Looking straight up or down - use forward based on yaw
            float yawRad = (float) Math.toRadians(player.getYRot());
            flatLook = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        } else {
            flatLook = flatLook.normalize();
        }

        Vec3 upVec = new Vec3(0, 1, 0);

        // Cross product: forward × up = right (in Minecraft's coordinate system)
        // This gives the vector pointing to player's right side
        Vec3 rightVec = flatLook.cross(upVec);

        // Position offsets for the pipe bowl in first person
        float forwardOffset = 0.5f;
        float downOffset = -0.15f;
        float rightOffset = 0.4f;  // Positive = right side where pipe is

        lastLocatorWorldPos = eyePos
                .add(flatLook.scale(forwardOffset))
                .add(upVec.scale(downOffset))
                .add(rightVec.scale(rightOffset));
    }

    /**
     * Get the last computed world position of the pipe's bowl locator.
     * Used by the particle system to spawn particles at the bowl.
     */
    public static Vec3 getLastLocatorWorldPos() {
        return lastLocatorWorldPos;
    }

    /**
     * Check if the local player is currently smoking in first-person view.
     * Used by the particle system to determine which particle mode to use.
     */
    public static boolean isCurrentlySmokingFirstPerson() {
        return isSmokingFirstPerson;
    }
}
