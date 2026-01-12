package frenk.eypipes.integration.rei;

import frenk.eypipes.recipe.DryingRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.List;

/**
 * REI display for drying rack recipes.
 * Reads from data-driven JSON recipes.
 */
public class DryingDisplay implements Display {

    private final ResourceLocation id;
    private final EntryIngredient input;
    private final EntryIngredient output;
    private final int dryingTime;

    public DryingDisplay(RecipeHolder<DryingRecipe> recipeHolder) {
        DryingRecipe recipe = recipeHolder.value();
        this.id = recipeHolder.id();
        this.input = EntryIngredients.ofIngredient(recipe.getInput());
        this.output = EntryIngredients.of(recipe.getOutput());
        this.dryingTime = recipe.getDryingTime();
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

    public ResourceLocation getId() {
        return id;
    }

    public int getDryingTime() {
        return dryingTime;
    }
}
