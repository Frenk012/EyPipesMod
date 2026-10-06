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
 * Recipe for the Tobacco Jar block.
 * Ferments herbs over time to increase quality.
 */
//? if <1.20.5 {
/*public class FermentingRecipe implements Recipe<Container> {
*///?} else
public class FermentingRecipe implements Recipe<SingleRecipeInput> {

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
    private final int fermentingTime; // in ticks
    private final int targetQuality; // 2 = aged, 3 = fermented

    public FermentingRecipe(Ingredient input, ItemStack output, int fermentingTime, int targetQuality) {
        this.input = input;
        this.output = output;
        this.fermentingTime = fermentingTime;
        this.targetQuality = targetQuality;
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
        return this.output.copy();
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
        return this.output.copy();
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
        return ModRecipes.FERMENTING_SERIALIZER.get();
    }

    @Override
    //? if <1.20.5 {
    /*public RecipeType<?> getType() {
    *///?} else
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
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

        //? if <1.20.5 {
        /*@Override
        public FermentingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getNonNull(json, "ingredient"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            FermentingRecipe recipe = new FermentingRecipe(input, output,
                    GsonHelper.getAsInt(json, "fermenting_time", 24000), GsonHelper.getAsInt(json, "target_quality", 2));
            recipe.id = id;
            return recipe;
        }

        @Override
        public FermentingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            FermentingRecipe recipe = new FermentingRecipe(Ingredient.fromNetwork(buffer), buffer.readItem(),
                    buffer.readVarInt(), buffer.readVarInt());
            recipe.id = id;
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, FermentingRecipe recipe) {
            recipe.getInput().toNetwork(buffer);
            buffer.writeItem(recipe.getOutput());
            buffer.writeVarInt(recipe.getFermentingTime());
            buffer.writeVarInt(recipe.getTargetQuality());
        }
        *///?} else {
        public static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        //? if <1.21.5 {
                        Ingredient.CODEC_NONEMPTY
                        //?} else
                        /*Ingredient.CODEC*/.fieldOf("ingredient").forGetter(FermentingRecipe::getInput),
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
        //?}
    }
}
