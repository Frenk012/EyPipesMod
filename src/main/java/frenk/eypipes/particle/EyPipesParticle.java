package frenk.eypipes.particle;

import net.minecraft.client.multiplayer.ClientLevel;
//? if <1.21.9 {
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
//?} else
/*import net.minecraft.client.particle.SingleQuadParticle;*/

/**
 * Base class for every EyPipes particle.
 *
 * <p>Minecraft 1.21.9 deleted {@code TextureSheetParticle} in favour of
 * {@code SingleQuadParticle}, and replaced {@code getRenderType} with a {@code getLayer} that
 * returns a render pipeline rather than a sheet. All six of this mod's particles are translucent
 * sprites from the particle atlas and differ only in how they move, so the superclass swap lives
 * here once instead of six times.
 */
//? if <1.21.9 {
public abstract class EyPipesParticle extends TextureSheetParticle {
//?} else
/*public abstract class EyPipesParticle extends SingleQuadParticle {*/

    protected EyPipesParticle(ClientLevel level, double x, double y, double z,
            double velX, double velY, double velZ) {
        //? if <1.21.9 {
        super(level, x, y, z, velX, velY, velZ);
        //?} else
        /*super(level, x, y, z, velX, velY, velZ, null);*/
    }

    //? if <1.21.9 {
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    //?} else {
    /*@Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }
    *///?}
}
