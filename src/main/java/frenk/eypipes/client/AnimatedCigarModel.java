package frenk.eypipes.client;

import frenk.eypipes.EyPipes;
import frenk.eypipes.item.custom.AnimatedCigar;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * GeoModel for the AnimatedCigar item using GeckoLib 3.x
 */
public class AnimatedCigarModel extends AnimatedGeoModel<AnimatedCigar> {
    
    @Override
    public Identifier getModelResource(AnimatedCigar object) {
        return new Identifier(EyPipes.MOD_ID, "geo/cigar.geo.json");
    }
    
    @Override
    public Identifier getTextureResource(AnimatedCigar object) {
        return new Identifier(EyPipes.MOD_ID, "textures/item/cigar.png");
    }
    
    @Override
    public Identifier getAnimationResource(AnimatedCigar animatable) {
        return new Identifier(EyPipes.MOD_ID, "animations/cigar.animation.json");
    }
}