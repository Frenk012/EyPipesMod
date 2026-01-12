package frenk.eypipes;

import frenk.eypipes.registries.ModDataComponents;
import frenk.eypipes.registries.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Client-side game bus event handlers for EyPipes.
 * These events are on the NeoForge game bus, not the mod bus.
 */
@EventBusSubscriber(modid = EyPipes.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class EyPipesClientEvents {

    /**
     * Called every client tick - can be used for client-side processing.
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // Client tick processing if needed
    }

    /**
     * Add fermentation level tooltip to fermentable items.
     */
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Check if item is a fermentable herb (dried or cutted variants)
        if (isFermentableItem(stack)) {
            // Check if it has a fermentation level
            if (stack.has(ModDataComponents.FERMENTATION_LEVEL.get())) {
                int level = stack.get(ModDataComponents.FERMENTATION_LEVEL.get());
                String qualityName = ModDataComponents.getQualityName(level);
                ChatFormatting color = getQualityColor(level);

                // Add quality tooltip
                event.getToolTip().add(Component.translatable("tooltip.eypipes.quality", qualityName)
                        .withStyle(color));

                // Add multiplier info
                float multiplier = ModDataComponents.getQualityMultiplier(level);
                String multiplierText = String.format("%.1fx", multiplier);
                event.getToolTip().add(Component.translatable("tooltip.eypipes.effect_multiplier", multiplierText)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    /**
     * Check if an item can be fermented / has fermentation data.
     */
    private static boolean isFermentableItem(ItemStack stack) {
        return stack.is(ModItems.ERBAPIPA_DRIED.get()) ||
               stack.is(ModItems.ERBAPIPA_CUTTED.get()) ||
               stack.is(ModItems.VALERIANA_DRIED.get()) ||
               stack.is(ModItems.VALERIANA_CUTTED.get()) ||
               stack.is(ModItems.GINSENG_DRIED.get()) ||
               stack.is(ModItems.GINSENG_CUTTED.get()) ||
               stack.is(ModItems.SALVIA_DRIED.get()) ||
               stack.is(ModItems.SALVIA_CUTTED.get());
    }

    /**
     * Get color for quality level.
     */
    private static ChatFormatting getQualityColor(int level) {
        return switch (level) {
            case ModDataComponents.QUALITY_FRESH -> ChatFormatting.WHITE;
            case ModDataComponents.QUALITY_AGED -> ChatFormatting.YELLOW;
            case ModDataComponents.QUALITY_FERMENTED -> ChatFormatting.GOLD;
            default -> ChatFormatting.GRAY; // DRIED
        };
    }
}
