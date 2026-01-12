package frenk.eypipes.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import frenk.eypipes.recipe.DryingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * EMI recipe wrapper for drying rack recipes.
 * Reads from data-driven JSON recipes.
 */
public class DryingEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final int dryingTime;

    public DryingEmiRecipe(RecipeHolder<DryingRecipe> recipeHolder) {
        DryingRecipe recipe = recipeHolder.value();
        this.id = recipeHolder.id();
        this.input = EmiIngredient.of(recipe.getInput());
        this.output = EmiStack.of(recipe.getOutput());
        this.dryingTime = recipe.getDryingTime();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EyPipesEMIPlugin.DRYING_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        // Input slot
        widgets.addSlot(input, 0, 10);

        // Arrow
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 27, 10);

        // Output slot
        widgets.addSlot(output, 60, 10).recipeContext(this);

        // Drying time text
        int dryingTimeSeconds = dryingTime / 20;
        widgets.addText(Component.literal(dryingTimeSeconds + "s"), 41, 30, 0x555555, false);
    }
}
