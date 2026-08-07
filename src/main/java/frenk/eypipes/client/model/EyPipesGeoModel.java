package frenk.eypipes.client.model;

import frenk.eypipes.EyPipes;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
//? if >=1.21.9
/*import software.bernie.geckolib.renderer.base.GeoRenderState;*/

/**
 * GeckoLib model for every animated EyPipes item.
 *
 * <p>All pipe variants and the cigar follow the same asset naming convention, so a single
 * data-driven model replaces the eleven near-identical model classes this mod used to have:
 * <ul>
 *   <li>{@code geo/<name>.geo.json}</li>
 *   <li>{@code textures/item/<name>.png}</li>
 *   <li>{@code animations/<name>.animation.json}</li>
 * </ul>
 *
 * <p>From GeckoLib 5.3 the model and texture are chosen from the captured render state rather
 * than from the animatable, since they are needed after the capture phase has ended. These
 * assets do not vary per instance, so both paths return the same thing.
 */
public class EyPipesGeoModel<T extends GeoAnimatable> extends GeoModel<T> {

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    /**
     * @param name the item's registry name, which is also its asset base name (e.g. {@code bent_pipe})
     */
    public EyPipesGeoModel(String name) {
        this.model = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "geo/" + name + ".geo.json");
        this.texture = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "textures/item/" + name + ".png");
        this.animation = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "animations/" + name + ".animation.json");
    }

    //? if <1.21.9 {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return texture;
    }
    //?} else {
    /*@Override
    public ResourceLocation getModelResource(GeoRenderState renderState) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoRenderState renderState) {
        return texture;
    }
    *///?}

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animation;
    }
}
