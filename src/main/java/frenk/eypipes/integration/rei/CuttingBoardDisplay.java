package frenk.eypipes.integration.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * REI display for cutting board recipes.
 * Shows dried herb + knife = cutted herbs.
 */
public class CuttingBoardDisplay implements Display {

    private final EntryIngredient input;
    private final EntryIngredient knife;
    private final EntryIngredient output;

    public CuttingBoardDisplay(ItemStack input, ItemStack knife, ItemStack output) {
        this.input = EntryIngredients.of(input);
        this.knife = EntryIngredients.of(knife);
        this.output = EntryIngredients.of(output);
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return Arrays.asList(input, knife);
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return Collections.singletonList(output);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EyPipesREIPlugin.CUTTING_BOARD_CATEGORY;
    }

    public EntryIngredient getInput() {
        return input;
    }

    public EntryIngredient getKnife() {
        return knife;
    }

    public EntryIngredient getOutput() {
        return output;
    }
}
