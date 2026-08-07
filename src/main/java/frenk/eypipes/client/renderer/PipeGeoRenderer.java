package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.client.layer.BurningTobaccoLayer;
import frenk.eypipes.client.model.EyPipesGeoModel;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.item.PipeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoItemRenderer;
//? if <1.21.9 {
import net.minecraft.client.renderer.MultiBufferSource;
//?} else {
/*import net.minecraft.client.renderer.state.CameraRenderState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
*///?}

/**
 * Renderer shared by every pipe variant.
 * Handles custom first-person positioning during use.
 * GeckoLib animation is disabled due to inconsistent behavior between dev/production.
 *
 * <p>All pipe variants differ only by their asset name, so one instance per variant
 * replaces the ten identical renderer subclasses this mod used to have.
 *
 * <p>Minecraft 1.21.9 split rendering into a capture phase and a submit phase, which are not
 * the same tick moment. Whether this pipe is lit, held in the left hand and seen in first person
 * can no longer be read while drawing, so it is captured into the render state and read back when
 * the pose is adjusted. The pose maths itself is identical on both paths.
 */
public class PipeGeoRenderer extends GeoItemRenderer<PipeItem> {

    // Shared static state for particle system (same for all pipe variants)
    private static Vec3 lastLocatorWorldPos = Vec3.ZERO;
    private static boolean isSmokingFirstPerson = false;
    private static boolean isLeftHand = false;

    // Track the current ItemStack being rendered and if it's being smoked
    private static ItemStack currentRenderingStack = ItemStack.EMPTY;
    private static boolean currentStackIsBeingSmoked = false;

    // Custom animation parameters (replaces unreliable GeckoLib animation)
    private static final float SMOKING_TRANSLATE_X = 0.0f;
    private static final float SMOKING_TRANSLATE_Y = 0.1f;
    private static final float SMOKING_TRANSLATE_Z = 0.4f;
    private static final float SMOKING_ROTATE_X = 10.0f;
    private static final float TRANSITION_TICKS = 10.0f;

    // Captured at render-state time and read back when the pose is adjusted, because the two
    // no longer happen at the same moment.
    //? if >=1.21.9 {
    /*private static final DataTicket<Float> SMOKING_PROGRESS =
            DataTicket.create("eypipes:smoking_progress", Float.class);

    private static final DataTicket<Boolean> LEFT_HAND_TICKET =
            DataTicket.create("eypipes:left_hand", Boolean.class);
    *///?}

    /**
     * @param name the pipe's registry name, which is also its asset base name (e.g. {@code bent_pipe})
     */
    public PipeGeoRenderer(String name) {
        super(new EyPipesGeoModel<>(name));
        // Add burning tobacco effect layer (glowing embers when smoking)
        //? if <1.21.9 {
        addRenderLayer(new BurningTobaccoLayer(this));
        //?} else
        /*withRenderLayer(new BurningTobaccoLayer(this));*/
    }

    /**
     * How far the pipe has been raised, 0 to 1, or 0 when it is not being smoked in first person.
     * Also refreshes the locator position the particle code reads.
     */
    private float captureSmokingState(ItemStack stack, ItemDisplayContext transformType) {
        boolean isFirstPerson = transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
                                transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        Player player = Minecraft.getInstance().player;
        boolean isSmoking = player != null && player.isUsingItem() && player.getUseItem() == stack;

        // Update shared state for BurningTobaccoLayer
        currentRenderingStack = stack;
        currentStackIsBeingSmoked = isSmoking;

        isSmokingFirstPerson = isFirstPerson && isSmoking;
        isLeftHand = transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        if (!isSmokingFirstPerson) {
            lastLocatorWorldPos = Vec3.ZERO;
            return 0.0f;
        }

        //? if <1.21.9 {
        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        //?} else
        /*float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);*/
        float smoothTicks = player.getTicksUsingItem() + partialTick;
        updateLocatorPosition(player, isLeftHand, partialTick);

        return Math.min(smoothTicks / TRANSITION_TICKS, 1.0f);
    }

    /** Lift and tilt the pipe towards the mouth. Shared by both render paths. */
    private static void applySmokingPose(PoseStack poseStack, float progress, boolean leftHand) {
        poseStack.translate(SMOKING_TRANSLATE_X * progress,
                SMOKING_TRANSLATE_Y * progress,
                SMOKING_TRANSLATE_Z * progress);
        poseStack.mulPose(Axis.XP.rotationDegrees(SMOKING_ROTATE_X * progress));

        if (leftHand) {
            poseStack.scale(-1.0f, 1.0f, 1.0f);
        }
    }

    //? if <1.21.9 {
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {

        float progress = captureSmokingState(stack, transformType);

        if (progress > 0.0f) {
            poseStack.pushPose();
            applySmokingPose(poseStack, progress, isLeftHand);
            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
    //?} else {
    /*@Override
    public void addRenderData(PipeItem animatable, RenderData relatedObject, GeoRenderState renderState,
            float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(SMOKING_PROGRESS,
                captureSmokingState(relatedObject.itemStack(), relatedObject.renderPerspective()));
        renderState.addGeckolibData(LEFT_HAND_TICKET, isLeftHand);
    }

    @Override
    public void adjustRenderPose(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model,
            CameraRenderState cameraState) {
        super.adjustRenderPose(renderState, poseStack, model, cameraState);

        float progress = renderState.getOrDefaultGeckolibData(SMOKING_PROGRESS, 0.0f);
        if (progress > 0.0f) {
            applySmokingPose(poseStack, progress, renderState.getOrDefaultGeckolibData(LEFT_HAND_TICKET, false));
        }
    }
    *///?}

    private void updateLocatorPosition(Player player, boolean leftHand, float partialTick) {
        if (player == null) return;

        Vec3 eyePos = player.getEyePosition(partialTick);
        Vec3 lookVec = player.getViewVector(partialTick);

        Vec3 flatLook = new Vec3(lookVec.x, 0, lookVec.z);
        if (flatLook.lengthSqr() < 0.0001) {
            float yawRad = (float) Math.toRadians(player.getYRot());
            flatLook = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        } else {
            flatLook = flatLook.normalize();
        }

        Vec3 upVec = new Vec3(0, 1, 0);
        Vec3 rightVec = flatLook.cross(upVec);

        // Use config values for particle position offsets
        float forwardOffset = EyPipesConfig.CLIENT.particleOffsetFirstViewForward.get().floatValue();
        float downOffset = EyPipesConfig.CLIENT.particleOffsetFirstViewDown.get().floatValue();
        float rightOffset = EyPipesConfig.CLIENT.particleOffsetFirstViewRight.get().floatValue();

        // Flip the right offset for left hand
        if (leftHand) {
            rightOffset = -rightOffset;
        }

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

    public static boolean isUsingLeftHand() {
        return isLeftHand;
    }

    /**
     * Check if the currently rendering pipe stack is being smoked.
     * Used by BurningTobaccoLayer to only show embers on the active pipe.
     */
    public static boolean isCurrentStackBeingSmoked() {
        return currentStackIsBeingSmoked;
    }

    /**
     * Get the ItemStack currently being rendered.
     */
    public static ItemStack getCurrentRenderingStack() {
        return currentRenderingStack;
    }
}
