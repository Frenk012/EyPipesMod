package frenk.eypipes.integration.epicfight;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

/**
 * Guard facade for Epic Fight integration.
 * Safe to reference from anywhere - all EF API calls are isolated in EpicFightAnimations.
 */
public class EpicFightCompat {

    private static boolean LOADED = false;

    public static void init(IEventBus modBus) {
        LOADED = ModList.get().isLoaded("epicfight");
        if (LOADED) {
            modBus.register(EpicFightAnimations.class);
        }
    }

    public static boolean isLoaded() {
        return LOADED;
    }

    /** Client-side: local player sees animation immediately + packet sent to server */
    public static void playSmokingClient(Player player, boolean isPipe) {
        if (!LOADED) return;
        EpicFightAnimations.playSmokingClient(player, isPipe);
    }

    /** Server-side: broadcasts SPAnimatorControl so all nearby clients see it */
    public static void playSmokingServer(ServerPlayer player, boolean isPipe) {
        if (!LOADED) return;
        EpicFightAnimations.playSmokingServer(player, isPipe);
    }

    public static void stopSmokingClient(Player player) {
        if (!LOADED) return;
        EpicFightAnimations.stopSmokingClient(player);
    }

    public static void stopSmokingServer(ServerPlayer player) {
        if (!LOADED) return;
        EpicFightAnimations.stopSmokingServer(player);
    }
}
