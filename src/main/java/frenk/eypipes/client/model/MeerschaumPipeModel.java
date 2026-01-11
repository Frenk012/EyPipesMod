package frenk.eypipes.client.model;

import frenk.eypipes.EyPipes;
import frenk.eypipes.item.PipeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MeerschaumPipeModel extends GeoModel<PipeItem> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "geo/meerschaum_pipe.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "textures/item/meerschaum_pipe.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "animations/meerschaum_pipe.animation.json");

    @Override
    public ResourceLocation getModelResource(PipeItem animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PipeItem animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PipeItem animatable) {
        return ANIMATION;
    }
}
