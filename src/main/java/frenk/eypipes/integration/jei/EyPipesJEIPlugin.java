package frenk.eypipes.integration.jei;

import frenk.eypipes.EyPipes;
import frenk.eypipes.recipe.CuttingBoardRecipe;
import frenk.eypipes.recipe.DryingRecipe;
import frenk.eypipes.recipe.FermentingRecipe;
import frenk.eypipes.recipe.ModRecipes;
import frenk.eypipes.registries.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

/**
 * JEI plugin for EyPipes mod.
 * Adds drying rack, tobacco jar, and cutting board recipe categories to JEI.
 * Bundle recipes use vanilla crafting format and appear automatically in JEI.
 */
@JeiPlugin
public class EyPipesJEIPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "jei_plugin");

    public static final RecipeType<DryingRecipe> DRYING_TYPE =
            RecipeType.create(EyPipes.MOD_ID, "drying", DryingRecipe.class);
    public static final RecipeType<FermentingRecipe> FERMENTING_TYPE =
            RecipeType.create(EyPipes.MOD_ID, "fermenting", FermentingRecipe.class);
    public static final RecipeType<CuttingBoardRecipe> CUTTING_BOARD_TYPE =
            RecipeType.create(EyPipes.MOD_ID, "cutting_board", CuttingBoardRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new DryingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FermentingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new CuttingBoardRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Register drying recipes from data-driven JSON
        List<DryingRecipe> dryingRecipes = recipeManager.getAllRecipesFor(ModRecipes.DRYING_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(DRYING_TYPE, dryingRecipes);

        // Register fermenting recipes from data-driven JSON
        List<FermentingRecipe> fermentingRecipes = recipeManager.getAllRecipesFor(ModRecipes.FERMENTING_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(FERMENTING_TYPE, fermentingRecipes);

        // Register cutting board recipes from data-driven JSON
        List<CuttingBoardRecipe> cuttingBoardRecipes = recipeManager.getAllRecipesFor(ModRecipes.CUTTING_BOARD_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(CUTTING_BOARD_TYPE, cuttingBoardRecipes);

        // Bundle recipes use vanilla crafting format and appear automatically in JEI
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.DRYING_RACK_ITEM.get()), DRYING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.TOBACCO_JAR_ITEM.get()), FERMENTING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.CUTTING_BOARD_ITEM.get()), CUTTING_BOARD_TYPE);
    }
}
