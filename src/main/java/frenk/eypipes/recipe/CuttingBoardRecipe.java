package frenk.eypipes.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Recipe for the Cutting Board block.
 * Cuts dried herbs into smaller pieces using a knife.
 */
public class CuttingBoardRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final int outputCount;

    public CuttingBoardRecipe(Ingredient input, ItemStack output, int outputCount) {
        this.input = input;
        this.output = output;
        this.outputCount = outputCount;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = this.output.copy();
        result.setCount(this.outputCount);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        ItemStack result = this.output.copy();
        result.setCount(this.outputCount);
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CUTTING_BOARD_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CUTTING_BOARD_TYPE.get();
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getOutputCount() {
        return outputCount;
    }

    /**
     * Serializer for CuttingBoardRecipe
     */
    public static class Serializer implements RecipeSerializer<CuttingBoardRecipe> {

        public static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CuttingBoardRecipe::getInput),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CuttingBoardRecipe::getOutput),
                        Codec.INT.optionalFieldOf("count", 4).forGetter(CuttingBoardRecipe::getOutputCount)
                ).apply(instance, CuttingBoardRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CuttingBoardRecipe::getInput,
                        ItemStack.STREAM_CODEC, CuttingBoardRecipe::getOutput,
                        net.minecraft.network.codec.ByteBufCodecs.INT, CuttingBoardRecipe::getOutputCount,
                        CuttingBoardRecipe::new
                );

        @Override
        public MapCodec<CuttingBoardRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
