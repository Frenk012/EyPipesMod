package frenk.eypipes.integration.rei;

import frenk.eypipes.EyPipes;
import frenk.eypipes.recipe.CuttingBoardRecipe;
import frenk.eypipes.recipe.DryingRecipe;
import frenk.eypipes.recipe.FermentingRecipe;
import frenk.eypipes.recipe.ModRecipes;
import frenk.eypipes.registries.ModItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * REI plugin for EyPipes mod.
 * Adds drying rack, tobacco jar, and cutting board recipe categories to REI.
 * Reads recipes from the data-driven recipe manager.
 */
@REIPluginClient
public class EyPipesREIPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<DryingDisplay> DRYING_CATEGORY =
            CategoryIdentifier.of(EyPipes.MOD_ID, "drying");
    public static final CategoryIdentifier<FermentingDisplay> FERMENTING_CATEGORY =
            CategoryIdentifier.of(EyPipes.MOD_ID, "fermenting");
    public static final CategoryIdentifier<CuttingBoardDisplay> CUTTING_BOARD_CATEGORY =
            CategoryIdentifier.of(EyPipes.MOD_ID, "cutting_board");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // Register drying rack category
        registry.add(new DryingCategory());
        registry.addWorkstations(DRYING_CATEGORY, EntryStacks.of(ModItems.DRYING_RACK_ITEM.get()));

        // Register tobacco jar fermenting category
        registry.add(new FermentingCategory());
        registry.addWorkstations(FERMENTING_CATEGORY, EntryStacks.of(ModItems.TOBACCO_JAR_ITEM.get()));

        // Register cutting board category
        registry.add(new CuttingBoardCategory());
        registry.addWorkstations(CUTTING_BOARD_CATEGORY, EntryStacks.of(ModItems.CUTTING_BOARD_ITEM.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {

        // Register drying recipes from data-driven JSON
        registry.registerRecipeFiller(DryingRecipe.class, ModRecipes.DRYING_TYPE.get(),
                DryingDisplay::new);

        // Register fermenting recipes from data-driven JSON
        registry.registerRecipeFiller(FermentingRecipe.class, ModRecipes.FERMENTING_TYPE.get(),
                FermentingDisplay::new);

        // Register cutting board recipes from data-driven JSON
        registry.registerRecipeFiller(CuttingBoardRecipe.class, ModRecipes.CUTTING_BOARD_TYPE.get(),
                CuttingBoardDisplay::new);
    }
}
