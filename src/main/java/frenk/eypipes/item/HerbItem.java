package frenk.eypipes.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Herb item with tooltip showing effect hints.
 * Used for fresh/raw herbs to give players a hint about smoking effects.
 */
public class HerbItem extends Item {

    private final String effectHintKey;
    private final ChatFormatting hintColor;

    /**
     * Create a herb item with effect hint tooltip.
     *
     * @param properties Item properties
     * @param effectHintKey Translation key for the effect hint (e.g., "item.eypipes.valeriana.hint")
     * @param hintColor Color for the hint text
     */
    public HerbItem(Properties properties, String effectHintKey, ChatFormatting hintColor) {
        super(properties);
        this.effectHintKey = effectHintKey;
        this.hintColor = hintColor;
    }

    //? if <1.21.5 {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        addEyPipesTooltip(stack, tooltipComponents::add);
    }
    //?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        addEyPipesTooltip(stack, tooltipAdder);
    }
    *///?}

    /** The mod's own tooltip lines, independent of how the game asks for them. */
    private void addEyPipesTooltip(ItemStack stack, java.util.function.Consumer<Component> lines) {

        // Add effect hint in italics with color
        lines.accept(Component.translatable(effectHintKey)
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(hintColor));
    }
}
