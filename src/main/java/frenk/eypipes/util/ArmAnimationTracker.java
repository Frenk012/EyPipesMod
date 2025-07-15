package frenk.eypipes.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to track arm animation positions for entities using pipes.
 * This allows synchronization between the mixin animation and particle spawning.
 */
public class ArmAnimationTracker {
    private static final Map<UUID, ArmPosition> armPositions = new ConcurrentHashMap<>();
    
    /**
     * Stores the current arm position for an entity.
     */
    public static void setArmPosition(LivingEntity entity, float pitch, float yaw) {
        if (entity != null) {
            armPositions.put(entity.getUuid(), new ArmPosition(pitch, yaw));
        }
    }
    
    /**
     * Gets the current arm position for an entity.
     */
    public static ArmPosition getArmPosition(LivingEntity entity) {
        if (entity != null) {
            return armPositions.get(entity.getUuid());
        }
        return null;
    }
    
    /**
     * Removes the arm position data for an entity (cleanup).
     */
    public static void removeArmPosition(LivingEntity entity) {
        if (entity != null) {
            armPositions.remove(entity.getUuid());
        }
    }
    

    private static final float PIPE_SMOKING_PITCH = -1.5F; // Target pitch for the arm when smoking
    private static final float PIPE_SMOKING_YAW = -0.5F; // Target yaw for the arm when smoking

    public static Vec3d calculatePipePosition(LivingEntity entity) {
        ArmPosition armPos = getArmPosition(entity);
        
        // Calculate pipe tip position based on arm rotation or default values
        Vec3d lookVec = entity.getRotationVec(1.0F);
        Vec3d rightVec = new Vec3d(-lookVec.z, 0, lookVec.x).normalize();
        Vec3d upVec = rightVec.crossProduct(lookVec).normalize();
        
        // Base position at entity's eye level
        Vec3d basePos = new Vec3d(
            entity.getX(),
            entity.getY() + entity.getEyeHeight(entity.getPose()),
            entity.getZ()
        );
        
        if (armPos == null) {
            // Fallback to default position with slight offset if no arm data
            // This ensures server-side positioning is still reasonable
            return basePos.add(
                lookVec.multiply(0.2)
                .add(rightVec.multiply(-0.1))
                .add(upVec.multiply(-0.05))
            );
        }
        
        // Apply the same smooth interpolation logic as the mixin
        // Calculate progress based on how close the arm is to the target position
        float pitchProgress = Math.abs(armPos.pitch / PIPE_SMOKING_PITCH);
        float yawProgress = Math.abs(armPos.yaw / PIPE_SMOKING_YAW);
        float smoothProgress = Math.min(1.0f, Math.max(pitchProgress, yawProgress));
        
        // Calculate pipe tip offset using the same interpolation approach
        // The pipe moves closer to the mouth as the animation progresses
        double basePipeLength = 0.3;
        double animatedPipeLength = basePipeLength * (1.0 - smoothProgress * 0.4); // Pipe gets closer
        
        // Apply smooth interpolated positioning that matches the arm movement
        Vec3d armOffset = lookVec.multiply(animatedPipeLength)
            .add(rightVec.multiply(-0.15 + (armPos.yaw / PIPE_SMOKING_YAW) * 0.1))
            .add(upVec.multiply(-0.05 + (armPos.pitch / PIPE_SMOKING_PITCH) * 0.15));
        
        return basePos.add(armOffset);
    }
    
    /**
     * Data class to store arm position information.
     */
    public static class ArmPosition {
        public final float pitch;
        public final float yaw;
        
        public ArmPosition(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }
}