package frenk.eypipes.recipe;

import frenk.eypipes.EyPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import frenk.eypipes.platform.Registrar;
import frenk.eypipes.platform.RegistryEntry;

/**
 * Registry for custom recipe types and serializers.
 */
public class ModRecipes {

    public static final Registrar<RecipeType<?>> RECIPE_TYPES = Registrar.create(Registries.RECIPE_TYPE);

    public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = Registrar.create(Registries.RECIPE_SERIALIZER);

    //? if >=1.21.5 {
    /*// From 1.21.5 Recipe#recipeBookCategory is mandatory. None of these recipes are made in
    // a vanilla recipe book menu, so they get an inert category of their own rather than
    // borrowing one that is actually displayed to the player.
    public static final Registrar<net.minecraft.world.item.crafting.RecipeBookCategory> RECIPE_BOOK_CATEGORIES = Registrar.create(Registries.RECIPE_BOOK_CATEGORY);

    public static final RegistryEntry<net.minecraft.world.item.crafting.RecipeBookCategory> PROCESSING_CATEGORY =
            RECIPE_BOOK_CATEGORIES.register("processing", net.minecraft.world.item.crafting.RecipeBookCategory::new);
    *///?}

    // Drying recipe type - for drying rack block
    public static final RegistryEntry<RecipeType<DryingRecipe>> DRYING_TYPE =
            RECIPE_TYPES.register("drying", () -> new RecipeType<DryingRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":drying";
                }
            });

    // Drying recipe serializer
    public static final RegistryEntry<DryingRecipe.Serializer> DRYING_SERIALIZER =
            RECIPE_SERIALIZERS.register("drying", DryingRecipe.Serializer::new);

    // Fermenting recipe type - for tobacco jar block
    public static final RegistryEntry<RecipeType<FermentingRecipe>> FERMENTING_TYPE =
            RECIPE_TYPES.register("fermenting", () -> new RecipeType<FermentingRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":fermenting";
                }
            });

    // Fermenting recipe serializer
    public static final RegistryEntry<FermentingRecipe.Serializer> FERMENTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fermenting", FermentingRecipe.Serializer::new);

    // Cutting board recipe type - for cutting board block
    public static final RegistryEntry<RecipeType<CuttingBoardRecipe>> CUTTING_BOARD_TYPE =
            RECIPE_TYPES.register("cutting_board", () -> new RecipeType<CuttingBoardRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":cutting_board";
                }
            });

    // Cutting board recipe serializer
    public static final RegistryEntry<CuttingBoardRecipe.Serializer> CUTTING_BOARD_SERIALIZER =
            RECIPE_SERIALIZERS.register("cutting_board", CuttingBoardRecipe.Serializer::new);

    // Herb bundle packing recipe type (9 herbs -> 1 bundle)
    public static final RegistryEntry<RecipeType<HerbBundlePackingRecipe>> HERB_BUNDLE_PACKING_TYPE =
            RECIPE_TYPES.register("herb_bundle_packing", () -> new RecipeType<HerbBundlePackingRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":herb_bundle_packing";
                }
            });

    // Herb bundle packing recipe serializer (9 herbs -> 1 bundle)
    public static final RegistryEntry<HerbBundlePackingRecipe.Serializer> HERB_BUNDLE_PACKING_SERIALIZER =
            RECIPE_SERIALIZERS.register("herb_bundle_packing", HerbBundlePackingRecipe.Serializer::new);

    // Herb bundle unpacking recipe type (1 bundle -> 9 herbs)
    public static final RegistryEntry<RecipeType<HerbBundleUnpackingRecipe>> HERB_BUNDLE_UNPACKING_TYPE =
            RECIPE_TYPES.register("herb_bundle_unpacking", () -> new RecipeType<HerbBundleUnpackingRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":herb_bundle_unpacking";
                }
            });

    // Herb bundle unpacking recipe serializer (1 bundle -> 9 herbs)
    public static final RegistryEntry<HerbBundleUnpackingRecipe.Serializer> HERB_BUNDLE_UNPACKING_SERIALIZER =
            RECIPE_SERIALIZERS.register("herb_bundle_unpacking", HerbBundleUnpackingRecipe.Serializer::new);

    /**
     * Register all recipe types and serializers.
     */
    /** Loads this class, which declares (and on Fabric registers) its entries. */
    public static void init() {
        EyPipes.LOGGER.info("Registering EyPipes Recipe Types");
    }
}
