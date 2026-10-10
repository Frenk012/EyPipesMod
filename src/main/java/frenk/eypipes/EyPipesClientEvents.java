package frenk.eypipes;

import frenk.eypipes.client.EyPipesClientCommon;
import frenk.eypipes.integration.epicfight.EpicFightCompat;
import frenk.eypipes.registries.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
//? if forge {
/*import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TickEvent;
*///?} else {
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
//?}
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Client-side game bus event handlers for EyPipes on (Neo)Forge; the behaviour itself lives in
 * {@link EyPipesClientCommon}, which the Fabric client entry point calls too.
 */
//? if forge {
/*@Mod.EventBusSubscriber(modid = EyPipes.MOD_ID, value = Dist.CLIENT)
*///?} else
@EventBusSubscriber(modid = EyPipes.MOD_ID, value = Dist.CLIENT)
public class EyPipesClientEvents {

    @SubscribeEvent
    //? if forge {
    /*public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
    *///?} else
    public static void onClientTick(ClientTickEvent.Post event) {
        EyPipesClientCommon.onClientTickEnd();
    }

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem();
        if (ModItems.isPipe(stack)) {
            EpicFightCompat.playSmokingClient(player, true);
        } else if (ModItems.isCigar(stack)) {
            EpicFightCompat.playSmokingClient(player, false);
        }
    }

    @SubscribeEvent
    public static void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (ModItems.isPipe(event.getItem()) || ModItems.isCigar(event.getItem())) {
            EpicFightCompat.stopSmokingClient(player);
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!EpicFightCompat.isLoaded()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (ModItems.isPipe(event.getItem()) || ModItems.isCigar(event.getItem())) {
            EpicFightCompat.stopSmokingClient(player);
        }
    }

    /** Adds the fermentation quality to dried and cut herbs. */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        EyPipesClientCommon.appendQualityTooltip(event.getItemStack(), event.getToolTip());
    }
}
