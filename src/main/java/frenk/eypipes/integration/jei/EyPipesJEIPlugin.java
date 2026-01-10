package frenk.eypipes.integration.jei;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * JEI plugin for EyPipes mod.
 * Adds drying rack recipe category to JEI.
 */
@JeiPlugin
public class EyPipesJEIPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_UID = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // Register drying rack recipe category
        registration.addRecipeCategories(
                new DryingCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Register drying recipes
        List<DryingRecipe> dryingRecipes = List.of(
                new DryingRecipe(
                        new ItemStack(ModItems.ERBAPIPA.get()),
                        new ItemStack(ModItems.ERBAPIPA_DRIED.get()),
                        200 // 10 seconds at 20 ticks/sec
                )
        );

        registration.addRecipes(DryingCategory.RECIPE_TYPE, dryingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Register drying rack as catalyst for drying recipes
        registration.addRecipeCatalyst(
                new ItemStack(ModItems.DRYING_RACK_ITEM.get()),
                DryingCategory.RECIPE_TYPE
        );
    }
}
