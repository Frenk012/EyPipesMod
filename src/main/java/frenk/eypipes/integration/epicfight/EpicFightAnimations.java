package frenk.eypipes.integration.epicfight;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationRegistryEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/**
 * Isolated Epic Fight API calls. Only classloaded when Epic Fight is present.
 * Do NOT reference this class directly - use EpicFightCompat instead.
 */
public class EpicFightAnimations {

    private static final Logger LOGGER = LoggerFactory.getLogger("EyPipes-EpicFight");

    public static AnimationAccessor<StaticAnimation> CIGAR_SMOKING;
    public static AnimationAccessor<StaticAnimation> PIPE_SMOKING;

    @SubscribeEvent
    public static void onAnimationRegistry(AnimationRegistryEvent event) {
        LOGGER.info("AnimationRegistryEvent fired - registering eypipes animations");
        event.newBuilder("eypipes", builder -> {
            CIGAR_SMOKING = builder.nextAccessor(
                "cigar_smoking",
                accessor -> new StaticAnimation(1.0f, true, accessor, Armatures.BIPED)
            );
            PIPE_SMOKING = builder.nextAccessor(
                "pipe_smoking",
                accessor -> new StaticAnimation(1.0f, true, accessor, Armatures.BIPED)
            );
            LOGGER.info("Registered: CIGAR_SMOKING={} PIPE_SMOKING={}", CIGAR_SMOKING, PIPE_SMOKING);
        });
    }

    /**
     * Called client-side for the LOCAL player.
     * Uses playAnimationInClientSide: plays immediately + sends packet to server for broadcast.
     */
    public static void playSmokingClient(Player player, boolean isPipe) {
        AnimationAccessor<StaticAnimation> anim = isPipe ? PIPE_SMOKING : CIGAR_SMOKING;
        LOGGER.info("playSmokingClient: isPipe={} anim={}", isPipe, anim);
        if (anim == null) { LOGGER.warn("Accessor is null - AnimationRegistryEvent may not have fired"); return; }
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        LOGGER.info("PlayerPatch={}", patch);
        if (patch != null) {
            patch.playAnimationInClientSide(anim, 0.0f);
            LOGGER.info("playAnimationInClientSide called");
        }
    }

    /**
     * Called server-side. Uses playAnimationSynchronized which broadcasts SPAnimatorControl
     * to all tracking clients (so other players see the animation).
     */
    public static void playSmokingServer(net.minecraft.server.level.ServerPlayer player, boolean isPipe) {
        AnimationAccessor<StaticAnimation> anim = isPipe ? PIPE_SMOKING : CIGAR_SMOKING;
        if (anim == null) return;
        yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch patch =
            EpicFightCapabilities.getServerPlayerPatch(player);
        if (patch != null) patch.playAnimationSynchronized(anim, 0.0f);
    }

    public static void stopSmokingClient(Player player) {
        PlayerPatch<?> patch = EpicFightCapabilities.getPlayerPatch(player);
        if (patch == null) return;
        if (CIGAR_SMOKING != null) patch.stopPlaying(CIGAR_SMOKING);
        if (PIPE_SMOKING  != null) patch.stopPlaying(PIPE_SMOKING);
    }

    public static void stopSmokingServer(net.minecraft.server.level.ServerPlayer player) {
        yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch patch =
            EpicFightCapabilities.getServerPlayerPatch(player);
        if (patch == null) return;
        if (CIGAR_SMOKING != null) patch.stopPlaying(CIGAR_SMOKING);
        if (PIPE_SMOKING  != null) patch.stopPlaying(PIPE_SMOKING);
    }
}
