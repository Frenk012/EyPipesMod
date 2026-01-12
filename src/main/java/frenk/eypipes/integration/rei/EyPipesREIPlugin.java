package frenk.eypipes.integration.rei;

import frenk.eypipes.EyPipes;
import frenk.eypipes.recipe.CuttingRecipe;
import frenk.eypipes.registries.ModItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * REI plugin for EyPipes mod.
 * Adds drying rack and tobacco jar recipe categories to REI.
 */
@REIPluginClient
public class EyPipesREIPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<DryingDisplay> DRYING_CATEGORY =
            CategoryIdentifier.of(EyPipes.MOD_ID, "drying");
    public static final CategoryIdentifier<FermentingDisplay> FERMENTING_CATEGORY =
            CategoryIdentifier.of(EyPipes.MOD_ID, "fermenting");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // Register drying rack category
        registry.add(new DryingCategory());
        registry.addWorkstations(DRYING_CATEGORY, EntryStacks.of(ModItems.DRYING_RACK_ITEM.get()));

        // Register tobacco jar fermenting category
        registry.add(new FermentingCategory());
        registry.addWorkstations(FERMENTING_CATEGORY, EntryStacks.of(ModItems.TOBACCO_JAR_ITEM.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // Register cutting recipes (dried -> cutted with shears)
        // Use recipe filler to automatically convert CuttingRecipe to crafting display
        registry.registerRecipeFiller(CuttingRecipe.class, RecipeType.CRAFTING,
                holder -> DefaultCraftingDisplay.of(holder));

        // Register drying recipes for all herbs
        registry.add(new DryingDisplay(
                new ItemStack(ModItems.ERBAPIPA.get()),
                new ItemStack(ModItems.ERBAPIPA_DRIED.get())
        ));
        registry.add(new DryingDisplay(
                new ItemStack(ModItems.VALERIANA.get()),
                new ItemStack(ModItems.VALERIANA_DRIED.get())
        ));
        registry.add(new DryingDisplay(
                new ItemStack(ModItems.GINSENG.get()),
                new ItemStack(ModItems.GINSENG_DRIED.get())
        ));
        registry.add(new DryingDisplay(
                new ItemStack(ModItems.SALVIA.get()),
                new ItemStack(ModItems.SALVIA_DRIED.get())
        ));

        // Register fermenting recipes for tobacco jar
        // Dried herbs can be fermented (Dried -> Aged -> Fermented)
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.ERBAPIPA_DRIED.get()),
                new ItemStack(ModItems.ERBAPIPA_DRIED.get()), // Same item, quality changes
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.ERBAPIPA_DRIED.get()),
                new ItemStack(ModItems.ERBAPIPA_DRIED.get()),
                "Fermented (3 days)"
        ));

        // Cutted herbs can also be fermented
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.ERBAPIPA_CUTTED.get()),
                new ItemStack(ModItems.ERBAPIPA_CUTTED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.ERBAPIPA_CUTTED.get()),
                new ItemStack(ModItems.ERBAPIPA_CUTTED.get()),
                "Fermented (3 days)"
        ));

        // Valeriana fermentation
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.VALERIANA_DRIED.get()),
                new ItemStack(ModItems.VALERIANA_DRIED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.VALERIANA_DRIED.get()),
                new ItemStack(ModItems.VALERIANA_DRIED.get()),
                "Fermented (3 days)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.VALERIANA_CUTTED.get()),
                new ItemStack(ModItems.VALERIANA_CUTTED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.VALERIANA_CUTTED.get()),
                new ItemStack(ModItems.VALERIANA_CUTTED.get()),
                "Fermented (3 days)"
        ));

        // Ginseng fermentation
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.GINSENG_DRIED.get()),
                new ItemStack(ModItems.GINSENG_DRIED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.GINSENG_DRIED.get()),
                new ItemStack(ModItems.GINSENG_DRIED.get()),
                "Fermented (3 days)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.GINSENG_CUTTED.get()),
                new ItemStack(ModItems.GINSENG_CUTTED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.GINSENG_CUTTED.get()),
                new ItemStack(ModItems.GINSENG_CUTTED.get()),
                "Fermented (3 days)"
        ));

        // Salvia fermentation
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.SALVIA_DRIED.get()),
                new ItemStack(ModItems.SALVIA_DRIED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.SALVIA_DRIED.get()),
                new ItemStack(ModItems.SALVIA_DRIED.get()),
                "Fermented (3 days)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.SALVIA_CUTTED.get()),
                new ItemStack(ModItems.SALVIA_CUTTED.get()),
                "Aged (1 day)"
        ));
        registry.add(new FermentingDisplay(
                new ItemStack(ModItems.SALVIA_CUTTED.get()),
                new ItemStack(ModItems.SALVIA_CUTTED.get()),
                "Fermented (3 days)"
        ));
    }
}
