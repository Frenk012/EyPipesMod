package frenk.eypipes.recipe;

import net.minecraft.core.NonNullList;
//? if <1.20.5 {
/*import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
*///?} else {
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Recipe for the Cutting Board block.
 * Cuts dried herbs into smaller pieces using a knife.
 */
//? if <1.20.5 {
/*public class CuttingBoardRecipe implements Recipe<Container> {
*///?} else
public class CuttingBoardRecipe implements Recipe<SingleRecipeInput> {

    //? if <1.20.5 {
    /*// Until 1.20.2 a recipe carries its own id; the serializer sets it once decoded
    private ResourceLocation id;

    @Override
    public ResourceLocation getId() {
        return id;
    }
    *///?}

    private final Ingredient input;
    private final ItemStack output;
    private final int outputCount;

    public CuttingBoardRecipe(Ingredient input, ItemStack output, int outputCount) {
        this.input = input;
        this.output = output;
        this.outputCount = outputCount;
    }

    @Override
    //? if <1.20.5 {
    /*public boolean matches(Container input, Level level) {
        return this.input.test(input.getItem(0));
    *///?} else {
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    //?}
    }

    @Override
    //? if <1.20.5 {
    /*public ItemStack assemble(Container input, RegistryAccess registries) {
    *///?} else
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        ItemStack result = this.output.copy();
        result.setCount(this.outputCount);
        return result;
    }

    //? if <1.21.5 {
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    //? if <1.20.5 {
    /*public ItemStack getResultItem(RegistryAccess registries) {
    *///?} else
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
    //?}

    //? if >=1.21.5 {
    /*@Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(this.input);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return ModRecipes.PROCESSING_CATEGORY.get();
    }
    *///?}

    @Override
    //? if <1.20.5 {
    /*public RecipeSerializer<?> getSerializer() {
    *///?} else
    public RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
        return ModRecipes.CUTTING_BOARD_SERIALIZER.get();
    }

    @Override
    //? if <1.20.5 {
    /*public RecipeType<?> getType() {
    *///?} else
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
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

        //? if <1.20.5 {
        /*@Override
        public CuttingBoardRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getNonNull(json, "ingredient"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            CuttingBoardRecipe recipe = new CuttingBoardRecipe(input, output, GsonHelper.getAsInt(json, "count", 4));
            recipe.id = id;
            return recipe;
        }

        @Override
        public CuttingBoardRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            CuttingBoardRecipe recipe = new CuttingBoardRecipe(Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readVarInt());
            recipe.id = id;
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CuttingBoardRecipe recipe) {
            recipe.getInput().toNetwork(buffer);
            buffer.writeItem(recipe.getOutput());
            buffer.writeVarInt(recipe.getOutputCount());
        }
        *///?} else {
        public static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        //? if <1.21.5 {
                        Ingredient.CODEC_NONEMPTY
                        //?} else
                        /*Ingredient.CODEC*/.fieldOf("ingredient").forGetter(CuttingBoardRecipe::getInput),
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
        //?}
    }
}
