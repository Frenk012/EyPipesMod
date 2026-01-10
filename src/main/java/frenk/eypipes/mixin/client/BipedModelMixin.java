package frenk.eypipes.mixin.client;

import frenk.eypipes.item.PipeItem;
import frenk.eypipes.item.CigarItem;
import frenk.eypipes.util.ArmAnimationTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to HumanoidModel to apply custom arm animations for pipe and cigar items.
 * Animates the player's right arm when using smoking items.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
@Mixin(HumanoidModel.class)
public abstract class BipedModelMixin<T extends LivingEntity> {

    @Shadow
    public ModelPart rightArm;

    // Constants for pipe smoking animation values
    private static final float PIPE_SMOKING_START_DELAY = 5.0f;
    private static final float PIPE_SMOKING_ANIMATION_DURATION = 20.0f;
    private static final float PIPE_SMOKING_PITCH = -1.5F;
    private static final float PIPE_SMOKING_YAW = -0.5F;

    /**
     * Injects custom animation logic into the setupAnim method of HumanoidModel.
     * This animates the player's right arm when they are using a PipeItem or CigarItem.
     */
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
                ArmAnimationTracker.removeArmPosition(entity);
                return;
            }

            int useTime = entity.getTicksUsingItem();

            // Calculate animation progress based on use time
            float progress = Math.max(0.0f, (useTime - PIPE_SMOKING_START_DELAY) / PIPE_SMOKING_ANIMATION_DURATION);
            float smoothProgress = Math.min(1.0f, progress);

            // Apply smooth interpolation to move arm to smoking position
            float currentPitch = smoothProgress * PIPE_SMOKING_PITCH;
            float currentYaw = smoothProgress * PIPE_SMOKING_YAW;

            this.rightArm.xRot = currentPitch;
            this.rightArm.yRot = currentYaw;

            // Track the arm position for particle synchronization
            ArmAnimationTracker.setArmPosition(entity, currentPitch, currentYaw);
        } else {
            // Clean up arm position tracking when not using smoking items
            ArmAnimationTracker.removeArmPosition(entity);
        }
    }
}
