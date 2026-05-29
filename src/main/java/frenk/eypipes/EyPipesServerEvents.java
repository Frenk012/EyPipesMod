package frenk.eypipes;

import frenk.eypipes.integration.epicfight.EpicFightCompat;
import frenk.eypipes.registries.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

/**
 * Server-side game-bus event handlers.
 * Handles Epic Fight animation broadcast so other players see the smoking animation.
 */
@EventBusSubscriber(modid = EyPipes.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EyPipesServerEvents {

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem();
        if (isPipeItem(stack)) {
            EpicFightCompat.playSmokingServer(player, true);
        } else if (isCigarItem(stack)) {
            EpicFightCompat.playSmokingServer(player, false);
        }
    }

    @SubscribeEvent
    public static void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isPipeItem(event.getItem()) || isCigarItem(event.getItem())) {
            EpicFightCompat.stopSmokingServer(player);
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (isPipeItem(event.getItem()) || isCigarItem(event.getItem())) {
            EpicFightCompat.stopSmokingServer(player);
        }
    }

    private static boolean isPipeItem(ItemStack stack) {
        return stack.is(ModItems.PIPE.get())
            || stack.is(ModItems.WOODEN_PIPE.get())
            || stack.is(ModItems.CLAY_PIPE.get())
            || stack.is(ModItems.CORN_COB_PIPE.get())
            || stack.is(ModItems.MEERSCHAUM_PIPE.get())
            || stack.is(ModItems.BRIAR_PIPE.get())
            || stack.is(ModItems.CHERRY_PIPE.get())
            || stack.is(ModItems.CALABASH_PIPE.get())
            || stack.is(ModItems.CHURCHWARD_PIPE.get())
            || stack.is(ModItems.BENT_PIPE.get());
    }

    private static boolean isCigarItem(ItemStack stack) {
        return stack.is(ModItems.CIGAR.get());
    }
}
