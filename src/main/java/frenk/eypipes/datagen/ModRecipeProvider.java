package frenk.eypipes.datagen;

import frenk.eypipes.registries.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;

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

        // === CUTTING RECIPES ===
        // These use CuttingRecipe which:
        // - Damages shears instead of consuming them
        // - Preserves fermentation level from input to output

        // Erbapipa cutted from dried
        CuttingRecipeBuilder.cutting(ModItems.ERBAPIPA_DRIED.get(), ModItems.ERBAPIPA_CUTTED.get(), 4)
                .unlockedBy("has_erbapipa_dried", has(ModItems.ERBAPIPA_DRIED.get()))
                .save(recipeOutput);

        // Valeriana cutted from dried
        CuttingRecipeBuilder.cutting(ModItems.VALERIANA_DRIED.get(), ModItems.VALERIANA_CUTTED.get(), 4)
                .unlockedBy("has_valeriana_dried", has(ModItems.VALERIANA_DRIED.get()))
                .save(recipeOutput);

        // Ginseng cutted from dried
        CuttingRecipeBuilder.cutting(ModItems.GINSENG_DRIED.get(), ModItems.GINSENG_CUTTED.get(), 4)
                .unlockedBy("has_ginseng_dried", has(ModItems.GINSENG_DRIED.get()))
                .save(recipeOutput);

        // Salvia cutted from dried
        CuttingRecipeBuilder.cutting(ModItems.SALVIA_DRIED.get(), ModItems.SALVIA_CUTTED.get(), 4)
                .unlockedBy("has_salvia_dried", has(ModItems.SALVIA_DRIED.get()))
                .save(recipeOutput);

        // Tobacco Jar recipe - clay pot style
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.TOBACCO_JAR_ITEM.get())
                .pattern("WLW")
                .pattern("B B")
                .pattern("BBB")
                .define('W', Items.OAK_PLANKS)
                .define('L', Items.OAK_SLAB)
                .define('B', Items.BRICK)
                .unlockedBy("has_erbapipa_dried", has(ModItems.ERBAPIPA_DRIED.get()))
                .save(recipeOutput);

        // Pipe Rack recipe - wooden wall mount
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.PIPE_RACK_ITEM.get())
                .pattern("WWW")
                .pattern("S S")
                .define('W', Items.OAK_PLANKS)
                .define('S', Items.STICK)
                .unlockedBy("has_pipe", has(ModItems.PIPE.get()))
                .save(recipeOutput);

        // === PIPE RECIPES ===

        // Wooden Pipe - basic oak pipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOODEN_PIPE.get())
                .pattern("  S")
                .pattern(" W ")
                .pattern("W  ")
                .define('S', Items.STICK)
                .define('W', Items.OAK_PLANKS)
                .unlockedBy("has_oak_planks", has(Items.OAK_PLANKS))
                .save(recipeOutput);

        // Clay Pipe - terracotta pipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLAY_PIPE.get())
                .pattern("  S")
                .pattern(" C ")
                .pattern("C  ")
                .define('S', Items.STICK)
                .define('C', Items.CLAY_BALL)
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(recipeOutput);

        // Corn Cob Pipe - made with dried kelp (representing corn cob)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CORN_COB_PIPE.get())
                .pattern("  S")
                .pattern(" K ")
                .pattern("W  ")
                .define('S', Items.STICK)
                .define('K', Items.DRIED_KELP_BLOCK)
                .define('W', Items.WHEAT)
                .unlockedBy("has_dried_kelp", has(Items.DRIED_KELP_BLOCK))
                .save(recipeOutput);

        // Meerschaum Pipe - white mineral pipe (quartz)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEERSCHAUM_PIPE.get())
                .pattern("  S")
                .pattern(" Q ")
                .pattern("Q  ")
                .define('S', Items.STICK)
                .define('Q', Items.QUARTZ)
                .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(recipeOutput);

        // Briar Pipe - dark hardwood pipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BRIAR_PIPE.get())
                .pattern("  S")
                .pattern(" D ")
                .pattern("D  ")
                .define('S', Items.STICK)
                .define('D', Items.DARK_OAK_PLANKS)
                .unlockedBy("has_dark_oak", has(Items.DARK_OAK_PLANKS))
                .save(recipeOutput);

        // Cherry Pipe - cherry wood pipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHERRY_PIPE.get())
                .pattern("  S")
                .pattern(" C ")
                .pattern("C  ")
                .define('S', Items.STICK)
                .define('C', Items.CHERRY_PLANKS)
                .unlockedBy("has_cherry", has(Items.CHERRY_PLANKS))
                .save(recipeOutput);

        // Calabash Pipe - gourd/pumpkin pipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CALABASH_PIPE.get())
                .pattern("  S")
                .pattern(" P ")
                .pattern("W  ")
                .define('S', Items.STICK)
                .define('P', Items.PUMPKIN)
                .define('W', Items.OAK_PLANKS)
                .unlockedBy("has_pumpkin", has(Items.PUMPKIN))
                .save(recipeOutput);

        // Churchward Pipe - long elegant pipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CHURCHWARD_PIPE.get())
                .pattern("  S")
                .pattern(" SS")
                .pattern("W  ")
                .define('S', Items.STICK)
                .define('W', Items.BIRCH_PLANKS)
                .unlockedBy("has_birch", has(Items.BIRCH_PLANKS))
                .save(recipeOutput);

        // Bent Pipe - curved pipe with iron fitting
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BENT_PIPE.get())
                .pattern("  S")
                .pattern(" I ")
                .pattern("W  ")
                .define('S', Items.STICK)
                .define('I', Items.IRON_NUGGET)
                .define('W', Items.SPRUCE_PLANKS)
                .unlockedBy("has_iron", has(Items.IRON_NUGGET))
                .save(recipeOutput);

        // Knife - herb cutting tool
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.KNIFE.get())
                .pattern("I")
                .pattern("S")
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(recipeOutput);

        // Cutting Board - for cutting herbs
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.CUTTING_BOARD_ITEM.get())
                .pattern("WWW")
                .define('W', Items.OAK_PLANKS)
                .unlockedBy("has_erbapipa_dried", has(ModItems.ERBAPIPA_DRIED.get()))
                .save(recipeOutput);
    }
}
