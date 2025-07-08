package frenk.eypipes.mixin.client;

import frenk.eypipes.item.PipeItem;
import frenk.eypipes.util.ArmAnimationTracker;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to BipedEntityModel to apply custom animations for the pipe item.
 */
@Mixin(BipedEntityModel.class)
public abstract class HumanoidEntityModelMixin<T extends LivingEntity> {

    @Shadow public net.minecraft.client.model.ModelPart rightArm;


    // Constants for pipe smoking animation values
    private static final float PIPE_SMOKING_START_DELAY = 5.0f; // Delay before animation starts in ticks
    private static final float PIPE_SMOKING_ANIMATION_DURATION = 20.0f; // Duration of the animation in ticks
    private static final float PIPE_SMOKING_PITCH = -1.5F; // Target pitch for the arm when smoking
    private static final float PIPE_SMOKING_YAW = -0.5F; // Target yaw for the arm when smoking

    /**
     * Injects custom animation logic into the positionRightArm method of BipedEntityModel.
     * This animates the player's right arm when they are using a PipeItem.
     * The animation moves the arm to a "close to face" position and holds it there
     * until the item usage finishes.
     */
    @Inject(method = "*", at = @At("TAIL"))
    private void animatePipeSmoking(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        ItemStack mainHandStack = entity.getStackInHand(Hand.MAIN_HAND);

        if (entity.isUsingItem() && mainHandStack.getItem() instanceof PipeItem) {
            int useTime = entity.getItemUseTime();
            
            // Calculate animation progress based on use time
            float progress = Math.max(0.0f, (useTime - PIPE_SMOKING_START_DELAY) / PIPE_SMOKING_ANIMATION_DURATION);
            float smoothProgress = Math.min(1.0f, progress);
            
            // Apply smooth interpolation to move arm to smoking position
            float currentPitch = smoothProgress * PIPE_SMOKING_PITCH;
            float currentYaw = smoothProgress * PIPE_SMOKING_YAW;
            
            this.rightArm.pitch = currentPitch;
            this.rightArm.yaw = currentYaw;
            
            // Track the arm position for particle synchronization
            ArmAnimationTracker.setArmPosition(entity, currentPitch, currentYaw);
        } else {
            // Clean up arm position tracking when not using pipe
            ArmAnimationTracker.removeArmPosition(entity);
        }
    }
}