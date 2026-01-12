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
 * Recipe for the Drying Rack block.
 * Converts fresh herbs into dried herbs over time.
 */
public class DryingRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final int dryingTime; // in ticks

    public DryingRecipe(Ingredient input, ItemStack output, int dryingTime) {
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.output.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.input);
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DRYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DRYING_TYPE.get();
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getDryingTime() {
        return dryingTime;
    }

    /**
     * Serializer for DryingRecipe
     */
    public static class Serializer implements RecipeSerializer<DryingRecipe> {

        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DryingRecipe::getInput),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(DryingRecipe::getOutput),
                        Codec.INT.optionalFieldOf("drying_time", 6000).forGetter(DryingRecipe::getDryingTime)
                ).apply(instance, DryingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, DryingRecipe::getInput,
                        ItemStack.STREAM_CODEC, DryingRecipe::getOutput,
                        net.minecraft.network.codec.ByteBufCodecs.INT, DryingRecipe::getDryingTime,
                        DryingRecipe::new
                );

        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
