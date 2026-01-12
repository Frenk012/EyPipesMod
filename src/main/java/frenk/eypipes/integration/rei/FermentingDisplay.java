package frenk.eypipes.integration.rei;

import frenk.eypipes.recipe.FermentingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.List;

/**
 * REI display for tobacco jar fermenting recipes.
 * Reads from data-driven JSON recipes.
 */
public class FermentingDisplay implements Display {

    private final ResourceLocation id;
    private final EntryIngredient input;
    private final EntryIngredient output;
    private final int targetQuality;
    private final String qualityLabel;

    public FermentingDisplay(RecipeHolder<FermentingRecipe> recipeHolder) {
        FermentingRecipe recipe = recipeHolder.value();
        this.id = recipeHolder.id();
        this.input = EntryIngredients.ofIngredient(recipe.getInput());
        this.output = EntryIngredients.of(recipe.getOutput());
        this.targetQuality = recipe.getTargetQuality();
        // Generate label based on target quality
        if (targetQuality == 3) {
            this.qualityLabel = "Fermented (3 days)";
        } else {
            this.qualityLabel = "Aged (1 day)";
        }
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

    public ResourceLocation getId() {
        return id;
    }

    public String getQualityLabel() {
        return qualityLabel;
    }

    public int getTargetQuality() {
        return targetQuality;
    }
}
