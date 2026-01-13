package frenk.eypipes.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import frenk.eypipes.EyPipes;
import frenk.eypipes.recipe.CuttingBoardRecipe;
import frenk.eypipes.recipe.DryingRecipe;
import frenk.eypipes.recipe.FermentingRecipe;
import frenk.eypipes.recipe.ModRecipes;
import frenk.eypipes.registries.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

/**
 * EMI plugin for EyPipes mod.
 * Adds drying rack, tobacco jar, and cutting board recipe categories to EMI.
 * Reads recipes from the data-driven recipe manager.
 */
@EmiEntrypoint
public class EyPipesEMIPlugin implements EmiPlugin {

    public static final ResourceLocation DRYING_ID = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "drying");
    public static final ResourceLocation FERMENTING_ID = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "fermenting");
    public static final ResourceLocation CUTTING_BOARD_ID = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "cutting_board");

    public static final EmiRecipeCategory DRYING_CATEGORY = new EmiRecipeCategory(
            DRYING_ID,
            EmiStack.of(ModItems.DRYING_RACK_ITEM.get())
    );

    public static final EmiRecipeCategory FERMENTING_CATEGORY = new EmiRecipeCategory(
            FERMENTING_ID,
            EmiStack.of(ModItems.TOBACCO_JAR_ITEM.get())
    );

    public static final EmiRecipeCategory CUTTING_BOARD_CATEGORY = new EmiRecipeCategory(
            CUTTING_BOARD_ID,
            EmiStack.of(ModItems.CUTTING_BOARD_ITEM.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        // Register categories
        registry.addCategory(DRYING_CATEGORY);
        registry.addCategory(FERMENTING_CATEGORY);
        registry.addCategory(CUTTING_BOARD_CATEGORY);

        // Add workstations
        registry.addWorkstation(DRYING_CATEGORY, EmiStack.of(ModItems.DRYING_RACK_ITEM.get()));
        registry.addWorkstation(FERMENTING_CATEGORY, EmiStack.of(ModItems.TOBACCO_JAR_ITEM.get()));
        registry.addWorkstation(CUTTING_BOARD_CATEGORY, EmiStack.of(ModItems.CUTTING_BOARD_ITEM.get()));

        // Get recipe manager
        RecipeManager recipeManager = registry.getRecipeManager();

        // Register drying recipes from data-driven JSON
        for (RecipeHolder<DryingRecipe> holder : recipeManager.getAllRecipesFor(ModRecipes.DRYING_TYPE.get())) {
            registry.addRecipe(new DryingEmiRecipe(holder));
        }

        // Register fermenting recipes from data-driven JSON
        for (RecipeHolder<FermentingRecipe> holder : recipeManager.getAllRecipesFor(ModRecipes.FERMENTING_TYPE.get())) {
            registry.addRecipe(new FermentingEmiRecipe(holder));
        }

        // Register cutting board recipes from data-driven JSON
        for (RecipeHolder<CuttingBoardRecipe> holder : recipeManager.getAllRecipesFor(ModRecipes.CUTTING_BOARD_TYPE.get())) {
            registry.addRecipe(new CuttingBoardEmiRecipe(holder));
        }

        // Bundle recipes use vanilla crafting format and appear automatically in EMI
    }
}
