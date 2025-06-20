package frenk.eypipes.particles;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.particle.DefaultParticleType;
import frenk.eypipes.EyPipes;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;

public class EyPipesParticleTypes {
    public static final DefaultParticleType RING_OF_SMOKE = FabricParticleTypes.simple();

    public static void registerParticleType(){
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(EyPipes.MOD_ID, "ring_of_smoke"), RING_OF_SMOKE);
    }
}
