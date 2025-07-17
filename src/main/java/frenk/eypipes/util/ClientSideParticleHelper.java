package frenk.eypipes.util;

import frenk.eypipes.EyPipes;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Client-side utility class for calculating particle positions based on player model arm rotation.
 * This ensures smoke particles follow the actual arm movement from the player model.
 */
public class ClientSideParticleHelper {
    
    /**
     * Calculates the smoke particle spawn position based on the player model's right arm rotation.
     * This method should only be called on the client side.
     */
    public static Vec3d calculateSmokePosition(LivingEntity entity) {
        return calculateSmokePosition(entity, true);
    }
    /**
     * Calculates the smoke particle spawn position based on the player model's right arm rotation.
     * This method should only be called on the client side.
     * @param isFirstPerson whether the particle is being rendered in first-person view
     */
    public static Vec3d calculateSmokePosition(LivingEntity entity, boolean isFirstPerson) {
        EyPipes.LOGGER.info("[DEBUG] calculateSmokePosition called with entity: {}, isFirstPerson: {}", entity.getName().getString(), isFirstPerson);
        
        try {
            EyPipes.LOGGER.info("[DEBUG] About to call ArmAnimationTracker.calculatePipePosition");
            
            Vec3d pipePosition = ArmAnimationTracker.calculatePipePosition(entity);
            EyPipes.LOGGER.info("[DEBUG] pipePosition == null: {}", pipePosition == null);
            EyPipes.LOGGER.info("[DEBUG] Calculated pipePosition: {}, isFirstPerson: {}, entity: {}", pipePosition, isFirstPerson, entity.getName().getString());
            
            if (pipePosition != null) {
                EyPipes.LOGGER.info("[DEBUG] IF condition PASSED");
                // Apply first-person vs third-person offset adjustment
                if (isFirstPerson == false) {
                    EyPipes.LOGGER.info("[DEBUG] Applying third-person offset adjustment");
                    pipePosition = pipePosition.add(EyPipesConfig.PARTICLE_OFFSET_THIRDVIEW_X, EyPipesConfig.PARTICLE_OFFSET_THIRDVIEW_Y, EyPipesConfig.PARTICLE_OFFSET_THIRDVIEW_Z);
                }
                EyPipes.LOGGER.info("Final pipePosition: {}", pipePosition);
                return pipePosition;
            } else {
                EyPipes.LOGGER.info("[DEBUG] IF condition FAILED");
            }
        } catch (Exception e) {
            // Fallback to default position if anything goes wrong
            EyPipes.LOGGER.error("Error calculating arm-based smoke position: " + e.getMessage(), e);
        }
        
        EyPipes.LOGGER.info("[DEBUG] Returning default smoke position");
        return getDefaultSmokePosition(entity, isFirstPerson);
    }
    

    
    /**
     * Fallback method for default smoke position calculation.
     */
    private static Vec3d getDefaultSmokePosition(LivingEntity entity) {
        return getDefaultSmokePosition(entity, true);
    }
    
    /**
     * Fallback method for default smoke position calculation.
     * @param isFirstPerson whether the particle is being rendered in first-person view
     */
    private static Vec3d getDefaultSmokePosition(LivingEntity entity, boolean isFirstPerson) {
        Vec3d lookVec = entity.getRotationVec(1.0F);
        Vec3d rightVec = new Vec3d(-lookVec.z, 0, lookVec.x).normalize();
        Vec3d upVec = rightVec.crossProduct(lookVec).normalize();
        
        Vec3d basePos = new Vec3d(
            entity.getX(),
            entity.getY() + entity.getEyeHeight(entity.getPose()),
            entity.getZ()
        );
        
        Vec3d result = basePos
            .add(lookVec.multiply(EyPipesConfig.PARTICLE_OFFSET_X))
            .add(rightVec.multiply(EyPipesConfig.PARTICLE_OFFSET_Y))
            .add(upVec.multiply(EyPipesConfig.PARTICLE_OFFSET_Z));
        
        return result;
    }
}