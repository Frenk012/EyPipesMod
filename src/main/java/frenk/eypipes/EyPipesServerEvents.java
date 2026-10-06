package frenk.eypipes;

import frenk.eypipes.integration.epicfight.EpicFightCompat;
import frenk.eypipes.registries.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
//? if forge {
/*import net.neoforged.fml.common.Mod;
*///?} else
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

/**
 * Server-side game-bus event handlers.
 * Handles Epic Fight animation broadcast so other players see the smoking animation.
 */
//? if forge {
/*@Mod.EventBusSubscriber(modid = EyPipes.MOD_ID)
*///?} else
@EventBusSubscriber(modid = EyPipes.MOD_ID)
public class EyPipesServerEvents {

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItem();
        if (ModItems.isPipe(stack)) {
            EpicFightCompat.playSmokingServer(player, true);
        } else if (ModItems.isCigar(stack)) {
            EpicFightCompat.playSmokingServer(player, false);
        }
    }

    @SubscribeEvent
    public static void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (ModItems.isPipe(event.getItem()) || ModItems.isCigar(event.getItem())) {
            EpicFightCompat.stopSmokingServer(player);
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (ModItems.isPipe(event.getItem()) || ModItems.isCigar(event.getItem())) {
            EpicFightCompat.stopSmokingServer(player);
        }
    }
}
