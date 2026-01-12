package frenk.eypipes.integration.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * REI display for tobacco jar fermenting recipes.
 */
public class FermentingDisplay implements Display {

    private final EntryIngredient input;
    private final EntryIngredient output;
    private final String qualityLabel;

    public FermentingDisplay(ItemStack input, ItemStack output, String qualityLabel) {
        this.input = EntryIngredients.of(input);
        this.output = EntryIngredients.of(output);
        this.qualityLabel = qualityLabel;
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
        return EyPipesREIPlugin.FERMENTING_CATEGORY;
    }

    public String getQualityLabel() {
        return qualityLabel;
    }
}
