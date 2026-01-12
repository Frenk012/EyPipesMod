package frenk.eypipes.recipe;

import frenk.eypipes.EyPipes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for custom recipe types and serializers.
 */
public class ModRecipes {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, EyPipes.MOD_ID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, EyPipes.MOD_ID);

    // Drying recipe type - for drying rack block
    public static final DeferredHolder<RecipeType<?>, RecipeType<DryingRecipe>> DRYING_TYPE =
            RECIPE_TYPES.register("drying", () -> new RecipeType<DryingRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":drying";
                }
            });

    // Drying recipe serializer
    public static final DeferredHolder<RecipeSerializer<?>, DryingRecipe.Serializer> DRYING_SERIALIZER =
            RECIPE_SERIALIZERS.register("drying", DryingRecipe.Serializer::new);

    // Fermenting recipe type - for tobacco jar block
    public static final DeferredHolder<RecipeType<?>, RecipeType<FermentingRecipe>> FERMENTING_TYPE =
            RECIPE_TYPES.register("fermenting", () -> new RecipeType<FermentingRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":fermenting";
                }
            });

    // Fermenting recipe serializer
    public static final DeferredHolder<RecipeSerializer<?>, FermentingRecipe.Serializer> FERMENTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("fermenting", FermentingRecipe.Serializer::new);

    // Cutting board recipe type - for cutting board block
    public static final DeferredHolder<RecipeType<?>, RecipeType<CuttingBoardRecipe>> CUTTING_BOARD_TYPE =
            RECIPE_TYPES.register("cutting_board", () -> new RecipeType<CuttingBoardRecipe>() {
                @Override
                public String toString() {
                    return EyPipes.MOD_ID + ":cutting_board";
                }
            });

    // Cutting board recipe serializer
    public static final DeferredHolder<RecipeSerializer<?>, CuttingBoardRecipe.Serializer> CUTTING_BOARD_SERIALIZER =
            RECIPE_SERIALIZERS.register("cutting_board", CuttingBoardRecipe.Serializer::new);

    /**
     * Register all recipe types and serializers.
     */
    public static void register(IEventBus eventBus) {
        EyPipes.LOGGER.info("Registering EyPipes Recipe Types");
        RECIPE_TYPES.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
