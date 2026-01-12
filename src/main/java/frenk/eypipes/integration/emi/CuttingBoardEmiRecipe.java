package frenk.eypipes.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import frenk.eypipes.recipe.CuttingBoardRecipe;
import frenk.eypipes.registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * EMI recipe wrapper for cutting board recipes.
 * Reads from data-driven JSON recipes.
 */
public class CuttingBoardEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiIngredient knife;
    private final EmiStack output;

    public CuttingBoardEmiRecipe(RecipeHolder<CuttingBoardRecipe> recipeHolder) {
        CuttingBoardRecipe recipe = recipeHolder.value();
        this.id = recipeHolder.id();
        this.input = EmiIngredient.of(recipe.getInput());
        this.knife = EmiStack.of(new ItemStack(ModItems.KNIFE.get()));
        ItemStack outputStack = recipe.getOutput().copy();
        outputStack.setCount(recipe.getOutputCount());
        this.output = EmiStack.of(outputStack);
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EyPipesEMIPlugin.CUTTING_BOARD_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input, knife);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Input herb slot
        widgets.addSlot(input, 0, 10);

        // Knife slot (tool, not consumed)
        widgets.addSlot(knife, 22, 10);

        // Arrow
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 45, 10);

        // Output slot
        widgets.addSlot(output, 78, 10).recipeContext(this);
    }
}
