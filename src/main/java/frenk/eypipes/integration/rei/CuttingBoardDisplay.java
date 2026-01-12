package frenk.eypipes.integration.rei;

import frenk.eypipes.recipe.CuttingBoardRecipe;
import frenk.eypipes.registries.ModItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * REI display for cutting board recipes.
 * Shows dried herb + knife = cutted herbs.
 * Reads from data-driven JSON recipes.
 */
public class CuttingBoardDisplay implements Display {

    private final ResourceLocation id;
    private final EntryIngredient input;
    private final EntryIngredient knife;
    private final EntryIngredient output;

    public CuttingBoardDisplay(RecipeHolder<CuttingBoardRecipe> recipeHolder) {
        CuttingBoardRecipe recipe = recipeHolder.value();
        this.id = recipeHolder.id();
        this.input = EntryIngredients.ofIngredient(recipe.getInput());
        this.knife = EntryIngredients.of(new ItemStack(ModItems.KNIFE.get()));
        ItemStack outputStack = recipe.getOutput().copy();
        outputStack.setCount(recipe.getOutputCount());
        this.output = EntryIngredients.of(outputStack);
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

    public ResourceLocation getId() {
        return id;
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
