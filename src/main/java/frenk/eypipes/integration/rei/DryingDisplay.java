package frenk.eypipes.integration.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * REI display for drying rack recipes.
 */
public class DryingDisplay implements Display {

    private final EntryIngredient input;
    private final EntryIngredient output;

    public DryingDisplay(ItemStack input, ItemStack output) {
        this.input = EntryIngredients.of(input);
        this.output = EntryIngredients.of(output);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return Collections.singletonList(input);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(output);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EyPipesREIPlugin.DRYING_CATEGORY;
    }
}
