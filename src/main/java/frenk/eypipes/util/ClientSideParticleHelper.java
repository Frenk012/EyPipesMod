package frenk.eypipes.util;

import frenk.eypipes.EyPipes;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.entity.LivingEntity;
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
        Vec3d pipePosition = ArmAnimationTracker.calculatePipePosition(entity);
        EyPipes.LOGGER.info("Calculated pipePosition: {}, isFirstPerson: {}, entity: {}", pipePosition, isFirstPerson, entity.getName().getString());
        try {
            if (pipePosition != null) {
                // Apply first-person vs third-person offset adjustment
                if (isFirstPerson == false) {
                    EyPipes.LOGGER.info("Applying third-person offset adjustment");
                    pipePosition = pipePosition.add(0.4, 0.33, 0);
                }
                return pipePosition;
            } else {
                EyPipes.LOGGER.info("IF condition FAILED");
            }
        } catch (Exception e) {
            // Fallback to default position if anything goes wrong
            EyPipes.LOGGER.error("Error calculating arm-based smoke position: " + e.getMessage(), e);
        }
        
        EyPipes.LOGGER.info("Returning default smoke position");
        return pipePosition;
    }
}