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
 * Recipe for the Tobacco Jar block.
 * Ferments herbs over time to increase quality.
 */
public class FermentingRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient input;
    private final ItemStack output;
    private final int fermentingTime; // in ticks
    private final int targetQuality; // 2 = aged, 3 = fermented

    public FermentingRecipe(Ingredient input, ItemStack output, int fermentingTime, int targetQuality) {
        this.input = input;
        this.output = output;
        this.fermentingTime = fermentingTime;
        this.targetQuality = targetQuality;
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
        return ModRecipes.FERMENTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FERMENTING_TYPE.get();
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getFermentingTime() {
        return fermentingTime;
    }

    public int getTargetQuality() {
        return targetQuality;
    }

    /**
     * Serializer for FermentingRecipe
     */
    public static class Serializer implements RecipeSerializer<FermentingRecipe> {

        public static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FermentingRecipe::getInput),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(FermentingRecipe::getOutput),
                        Codec.INT.optionalFieldOf("fermenting_time", 24000).forGetter(FermentingRecipe::getFermentingTime),
                        Codec.INT.optionalFieldOf("target_quality", 2).forGetter(FermentingRecipe::getTargetQuality)
                ).apply(instance, FermentingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, FermentingRecipe::getInput,
                        ItemStack.STREAM_CODEC, FermentingRecipe::getOutput,
                        net.minecraft.network.codec.ByteBufCodecs.INT, FermentingRecipe::getFermentingTime,
                        net.minecraft.network.codec.ByteBufCodecs.INT, FermentingRecipe::getTargetQuality,
                        FermentingRecipe::new
                );

        @Override
        public MapCodec<FermentingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
