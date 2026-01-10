package frenk.eypipes.client;

import frenk.eypipes.EyPipes;
import frenk.eypipes.item.PipeItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class AnimatedPipeModel extends AnimatedGeoModel<PipeItem> {
    @Override
    public Identifier getModelResource(PipeItem object) {
        return new Identifier(EyPipes.MOD_ID, "geo/pipe.geo.json");
    }

    @Override
    public Identifier getTextureResource(PipeItem object) {
        return new Identifier(EyPipes.MOD_ID, "textures/item/pipe.png");
    }

    @Override
    public Identifier getAnimationResource(PipeItem animatable) {
        return new Identifier(EyPipes.MOD_ID, "animations/pipe.animation.json");
    }
}