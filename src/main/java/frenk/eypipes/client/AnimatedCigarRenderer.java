package frenk.eypipes.client;

import frenk.eypipes.item.custom.AnimatedCigar;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

/**
 * GeoItemRenderer for the AnimatedCigar using GeckoLib 3.x
 * Simple implementation that relies on GeckoLib's built-in rendering system
 */
public class AnimatedCigarRenderer extends GeoItemRenderer<AnimatedCigar> {
    
    public AnimatedCigarRenderer() {
        super(new AnimatedCigarModel());
    }
}