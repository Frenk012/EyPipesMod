package frenk.eypipes.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import frenk.eypipes.recipe.FermentingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * EMI recipe wrapper for tobacco jar fermenting recipes.
 * Reads from data-driven JSON recipes.
 */
public class FermentingEmiRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final EmiIngredient input;
    private final EmiStack output;
    private final int fermentingTime;
    private final int targetQuality;

    public FermentingEmiRecipe(RecipeHolder<FermentingRecipe> recipeHolder) {
        FermentingRecipe recipe = recipeHolder.value();
        this.id = recipeHolder.id();
        this.input = EmiIngredient.of(recipe.getInput());
        this.output = EmiStack.of(recipe.getOutput());
        this.fermentingTime = recipe.getFermentingTime();
        this.targetQuality = recipe.getTargetQuality();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EyPipesEMIPlugin.FERMENTING_CATEGORY;
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
        return 100;
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

        // Time label based on target quality
        String timeLabel;
        if (targetQuality == 3) {
            timeLabel = "Fermented (3 days)";
        } else {
            timeLabel = "Aged (1 day)";
        }
        widgets.addText(Component.literal(timeLabel), 0, 30, 0x555555, false);
    }
}
