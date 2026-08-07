package frenk.eypipes.client.model;

import frenk.eypipes.EyPipes;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib model for every animated EyPipes item.
 *
 * All pipe variants and the cigar follow the same asset naming convention, so a single
 * data-driven model replaces the eleven near-identical model classes this mod used to have:
 * <ul>
 *   <li>{@code geo/<name>.geo.json}</li>
 *   <li>{@code textures/item/<name>.png}</li>
 *   <li>{@code animations/<name>.animation.json}</li>
 * </ul>
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

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animation;
    }
}
