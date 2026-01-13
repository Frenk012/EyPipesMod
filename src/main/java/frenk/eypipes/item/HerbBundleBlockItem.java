package frenk.eypipes.item;

import frenk.eypipes.registries.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Block item for herb bundles that displays fermentation quality in tooltip.
 * The fermentation level is stored as a data component.
 */
public class HerbBundleBlockItem extends BlockItem {

    private final ChatFormatting herbColor;

    public HerbBundleBlockItem(Block block, Properties properties, ChatFormatting herbColor) {
        super(block, properties);
        this.herbColor = herbColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        // Show quality level if present
        if (stack.has(ModDataComponents.FERMENTATION_LEVEL.get())) {
            int qualityLevel = stack.get(ModDataComponents.FERMENTATION_LEVEL.get());
            String qualityName = ModDataComponents.getQualityName(qualityLevel);
            ChatFormatting qualityColor = getQualityColor(qualityLevel);

            tooltipComponents.add(Component.translatable("tooltip.eypipes.quality",
                    Component.translatable("tooltip.eypipes.quality." + qualityName.toLowerCase()).withStyle(qualityColor))
                    .withStyle(ChatFormatting.GRAY));
        }

        // Show mod name
        tooltipComponents.add(Component.translatable("itemGroup.eypipes.eypipes_tab")
                .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));
    }

    /**
     * Get the color formatting for a quality level.
     */
    private ChatFormatting getQualityColor(int qualityLevel) {
        return switch (qualityLevel) {
            case 0 -> ChatFormatting.GRAY;           // Fresh
            case 2 -> ChatFormatting.YELLOW;         // Aged
            case 3 -> ChatFormatting.GOLD;           // Fermented
            default -> ChatFormatting.WHITE;         // Dried
        };
    }

    public ChatFormatting getHerbColor() {
        return herbColor;
    }
}
