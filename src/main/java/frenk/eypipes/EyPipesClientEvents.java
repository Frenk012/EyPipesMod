package frenk.eypipes;

import frenk.eypipes.particle.ParticleScheduler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Client-side game bus event handlers for EyPipes.
 * These events are on the NeoForge game bus, not the mod bus.
 */
@EventBusSubscriber(modid = EyPipes.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EyPipesClientEvents {

    /**
     * Called every client tick - used for scheduled particle processing.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ParticleScheduler.tick();
    }
}
