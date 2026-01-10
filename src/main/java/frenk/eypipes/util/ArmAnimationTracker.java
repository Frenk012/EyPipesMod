package frenk.eypipes.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to track arm animation positions for entities using pipes.
 * This allows synchronization between the mixin animation and particle spawning.
 * Ported from Fabric 1.19.2 to NeoForge 1.21.1.
 */
public class ArmAnimationTracker {
    private static final Map<UUID, ArmPosition> armPositions = new ConcurrentHashMap<>();

    private static final float PIPE_SMOKING_PITCH = -1.5F;
    private static final float PIPE_SMOKING_YAW = -0.5F;

    /**
     * Stores the current arm position for an entity.
     */
    public static void setArmPosition(LivingEntity entity, float pitch, float yaw) {
        if (entity != null) {
            armPositions.put(entity.getUUID(), new ArmPosition(pitch, yaw));
        }
    }

    /**
     * Gets the current arm position for an entity.
     */
    public static ArmPosition getArmPosition(LivingEntity entity) {
        if (entity != null) {
            return armPositions.get(entity.getUUID());
        }
        return null;
    }

    /**
     * Removes the arm position data for an entity (cleanup).
     */
    public static void removeArmPosition(LivingEntity entity) {
        if (entity != null) {
            armPositions.remove(entity.getUUID());
        }
    }

    /**
     * Calculates the pipe position based on entity pose and arm animation.
     */
    public static Vec3 calculatePipePosition(LivingEntity entity) {
        ArmPosition armPos = getArmPosition(entity);

        // Calculate direction vectors based on entity's current looking direction
        Vec3 lookVec = entity.getViewVector(1.0F);
        Vec3 rightVec = new Vec3(lookVec.z, 0, -lookVec.x).normalize();
        Vec3 upVec = rightVec.cross(lookVec).normalize();

        // Base position at entity's eye level
        Vec3 basePos = new Vec3(
                entity.getX(),
                entity.getY() + entity.getEyeHeight(),
                entity.getZ()
        );

        if (armPos == null) {
            // Fallback to default position that follows player's looking direction
            return basePos.add(
                    lookVec.scale(0.2)
                            .add(rightVec.scale(-0.1))
                            .add(upVec.scale(-0.05))
            );
        }

        // Calculate progress based on how close the arm is to the target position
        float pitchProgress = Math.abs(armPos.pitch / PIPE_SMOKING_PITCH);
        float yawProgress = Math.abs(armPos.yaw / PIPE_SMOKING_YAW);
        float smoothProgress = Math.min(1.0f, Math.max(pitchProgress, yawProgress));

        // The pipe moves closer to the mouth as the animation progresses
        double basePipeLength = 0.3;
        double animatedPipeLength = basePipeLength * (1.0 - smoothProgress * 0.4);

        // Use the direction vectors to ensure the pipe position rotates with player view
        Vec3 armOffset = lookVec.scale(animatedPipeLength)
                .add(rightVec.scale(-0.15 + (armPos.yaw / PIPE_SMOKING_YAW) * 0.1))
                .add(upVec.scale(-0.05 + (armPos.pitch / PIPE_SMOKING_PITCH) * 0.15));

        return basePos.add(armOffset);
    }

    public static class ArmPosition {
        public final float pitch;
        public final float yaw;

        public ArmPosition(float pitch, float yaw) {
            this.pitch = pitch;
            this.yaw = yaw;
        }
    }
}
