package frenk.eypipes.client;

import frenk.eypipes.client.layer.BurningTobaccoLayer;
import frenk.eypipes.registries.ModDataComponents;
import frenk.eypipes.registries.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Client behaviour every loader shares. The (Neo)Forge event subscribers and the Fabric client
 * entry point only decide when these run.
 */
public final class EyPipesClientCommon {
    private EyPipesClientCommon() {
    }

    /** How often the afterglow tracking map is swept, in client ticks. */
    private static final int AFTERGLOW_SWEEP_INTERVAL = 600;

    /** Runs at the end of every client tick. */
    public static void onClientTickEnd() {
        // Afterglow entries are keyed by stack identity, so pipes that are dropped, destroyed or
        // unloaded never get cleared by the render path. Sweep them periodically.
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && level.getGameTime() % AFTERGLOW_SWEEP_INTERVAL == 0) {
            BurningTobaccoLayer.cleanupOldEntries(level.getGameTime());
        }
    }

    /** Adds the fermentation quality and its effect multiplier to dried and cut herbs. */
    public static void appendQualityTooltip(ItemStack stack, List<Component> tooltip) {
        if (!isFermentable(stack) || !ModDataComponents.FERMENTATION_LEVEL.has(stack)) {
            return;
        }

        int level = ModDataComponents.FERMENTATION_LEVEL.get(stack);
        String qualityName = ModDataComponents.getQualityName(level);
        tooltip.add(Component.translatable("tooltip.eypipes.quality", qualityName)
                .withStyle(qualityColor(level)));

        String multiplierText = String.format("%.1fx", ModDataComponents.getQualityMultiplier(level));
        tooltip.add(Component.translatable("tooltip.eypipes.effect_multiplier", multiplierText)
                .withStyle(ChatFormatting.GRAY));
    }

    /** Whether an item can be fermented and so carries fermentation data. */
    private static boolean isFermentable(ItemStack stack) {
        return stack.is(ModItems.ERBAPIPA_DRIED.get()) ||
               stack.is(ModItems.ERBAPIPA_CUTTED.get()) ||
               stack.is(ModItems.VALERIANA_DRIED.get()) ||
               stack.is(ModItems.VALERIANA_CUTTED.get()) ||
               stack.is(ModItems.GINSENG_DRIED.get()) ||
               stack.is(ModItems.GINSENG_CUTTED.get()) ||
               stack.is(ModItems.SALVIA_DRIED.get()) ||
               stack.is(ModItems.SALVIA_CUTTED.get());
    }

    private static ChatFormatting qualityColor(int level) {
        return switch (level) {
            case ModDataComponents.QUALITY_FRESH -> ChatFormatting.WHITE;
            case ModDataComponents.QUALITY_AGED -> ChatFormatting.YELLOW;
            case ModDataComponents.QUALITY_FERMENTED -> ChatFormatting.GOLD;
            default -> ChatFormatting.GRAY; // DRIED
        };
    }
}
