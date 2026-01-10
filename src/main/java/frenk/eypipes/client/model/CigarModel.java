package frenk.eypipes.client.model;

import frenk.eypipes.EyPipes;
import frenk.eypipes.item.CigarItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib 4 model for the CigarItem.
 * Ported from GeckoLib 3's AnimatedGeoModel to GeckoLib 4's GeoModel.
 */
public class CigarModel extends GeoModel<CigarItem> {

    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "geo/cigar.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "textures/item/cigar.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "animations/cigar.animation.json");

    @Override
    public ResourceLocation getModelResource(CigarItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(CigarItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(CigarItem animatable) {
        return ANIMATION;
    }
}
