package frenk.eypipes.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if <1.21.9 {
import frenk.eypipes.item.PipeItem;
import frenk.eypipes.item.CigarItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
//?} else {
/*import frenk.eypipes.client.SmokingRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
*///?}

/**
 * Mixin to HumanoidModel to apply custom arm animations for pipe and cigar items.
 * Animates the player's right arm when using smoking items.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 *
 * <p>From 1.21.9 the model is posed from a render state that carries no {@code ItemStack}, so
 * whether a pipe is being smoked has to be recorded onto that state while it is extracted.
 * See {@link LivingEntityRendererMixin} and {@link frenk.eypipes.client.SmokingRenderState}.
 */
@Mixin(HumanoidModel.class)
//? if <1.21.9 {
public abstract class BipedModelMixin<T extends LivingEntity> {
//?} else
/*public abstract class BipedModelMixin {*/

    @Shadow
    public ModelPart rightArm;

    // Constants for pipe smoking animation values
    private static final float PIPE_SMOKING_START_DELAY = 5.0f;
    private static final float PIPE_SMOKING_ANIMATION_DURATION = 20.0f;
    private static final float PIPE_SMOKING_PITCH = -1.5F;
    private static final float PIPE_SMOKING_YAW = -0.5F;

    /** Raise the arm towards the mouth over the first second of use. */
    private void eypipes$poseSmokingArm(int useTime) {
        float progress = Math.max(0.0f, (useTime - PIPE_SMOKING_START_DELAY) / PIPE_SMOKING_ANIMATION_DURATION);
        float smoothProgress = Math.min(1.0f, progress);

        this.rightArm.xRot = smoothProgress * PIPE_SMOKING_PITCH;
        this.rightArm.yRot = smoothProgress * PIPE_SMOKING_YAW;
    }

    //? if <1.21.9 {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void animateSmokingItems(T entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {

        ItemStack mainHandStack = entity.getItemInHand(InteractionHand.MAIN_HAND);

        boolean isSmokingItem = mainHandStack.getItem() instanceof PipeItem ||
                mainHandStack.getItem() instanceof CigarItem;

        if (entity.isUsingItem() && isSmokingItem) {
            // Skip first-person animation for local player
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.getCameraType().isFirstPerson() && entity == mc.player) {
                return;
            }

            eypipes$poseSmokingArm(entity.getTicksUsingItem());
        }
    }
    //?} else {
    /*@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void animateSmokingItems(HumanoidRenderState renderState, CallbackInfo ci) {
        if (!(renderState instanceof SmokingRenderState smokingState)) {
            return;
        }

        int useTime = smokingState.eypipes$getSmokingTicks();
        if (useTime >= 0) {
            eypipes$poseSmokingArm(useTime);
        }
    }
    *///?}
}
