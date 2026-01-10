package frenk.eypipes.integration.jei;

import net.minecraft.world.item.ItemStack;

/**
 * Simple recipe data holder for JEI drying rack recipes.
 * Records the input item, output item, and drying time in ticks.
 */
public record DryingRecipe(ItemStack input, ItemStack output, int dryingTime) {

    /**
     * Gets the drying time in seconds for display purposes.
     */
    public float getDryingTimeSeconds() {
        return dryingTime / 20.0f;
    }
}
