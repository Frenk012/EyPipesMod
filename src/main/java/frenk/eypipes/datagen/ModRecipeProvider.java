package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

/**
 * Recipe provider for EyPipes items.
 * Generates crafting recipes for pipe, cigar, and drying rack.
 */
public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Pipe recipe - wooden bowl shape with stick
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PIPE.get())
                .pattern(" S ")
                .pattern("WBW")
                .pattern(" W ")
                .define('S', Items.STICK)
                .define('W', Items.OAK_PLANKS)
                .define('B', Items.BOWL)
                .unlockedBy("has_bowl", has(Items.BOWL))
                .save(recipeOutput);

        // Cigar recipe - erbapipa dried wrapped in paper
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIGAR.get())
                .pattern("DDD")
                .pattern("PPP")
                .define('D', ModItems.ERBAPIPA_DRIED.get())
                .define('P', Items.PAPER)
                .unlockedBy("has_erbapipa_dried", has(ModItems.ERBAPIPA_DRIED.get()))
                .save(recipeOutput);

        // Drying rack recipe - wooden frame
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.DRYING_RACK_ITEM.get())
                .pattern("SSS")
                .pattern("W W")
                .pattern("WWW")
                .define('S', Items.STICK)
                .define('W', Items.OAK_PLANKS)
                .unlockedBy("has_erbapipa", has(ModItems.ERBAPIPA.get()))
                .save(recipeOutput);

        // Erbapipa cutted from dried (cutting with shears equivalent)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ERBAPIPA_CUTTED.get(), 4)
                .requires(ModItems.ERBAPIPA_DRIED.get())
                .requires(Items.SHEARS)
                .unlockedBy("has_erbapipa_dried", has(ModItems.ERBAPIPA_DRIED.get()))
                .save(recipeOutput);
    }
}
