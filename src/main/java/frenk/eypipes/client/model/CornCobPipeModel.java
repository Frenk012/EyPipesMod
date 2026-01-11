package frenk.eypipes.client.model;

import frenk.eypipes.EyPipes;
import frenk.eypipes.item.PipeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CornCobPipeModel extends GeoModel<PipeItem> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "geo/corn_cob_pipe.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "textures/item/corn_cob_pipe.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "animations/corn_cob_pipe.animation.json");

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
