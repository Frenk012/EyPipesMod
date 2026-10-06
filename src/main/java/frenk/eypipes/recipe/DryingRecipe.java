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
 * Recipe for the Drying Rack block.
 * Converts fresh herbs into dried herbs over time.
 */
//? if <1.20.5 {
/*public class DryingRecipe implements Recipe<Container> {
*///?} else
public class DryingRecipe implements Recipe<SingleRecipeInput> {

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
    private final int dryingTime; // in ticks

    public DryingRecipe(Ingredient input, ItemStack output, int dryingTime) {
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
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
        return ModRecipes.DRYING_SERIALIZER.get();
    }

    @Override
    //? if <1.20.5 {
    /*public RecipeType<?> getType() {
    *///?} else
    public RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
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

        //? if <1.20.5 {
        /*@Override
        public DryingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getNonNull(json, "ingredient"));
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            DryingRecipe recipe = new DryingRecipe(input, output, GsonHelper.getAsInt(json, "drying_time", 6000));
            recipe.id = id;
            return recipe;
        }

        @Override
        public DryingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            DryingRecipe recipe = new DryingRecipe(Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readVarInt());
            recipe.id = id;
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, DryingRecipe recipe) {
            recipe.getInput().toNetwork(buffer);
            buffer.writeItem(recipe.getOutput());
            buffer.writeVarInt(recipe.getDryingTime());
        }
        *///?} else {
        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        //? if <1.21.5 {
                        Ingredient.CODEC_NONEMPTY
                        //?} else
                        /*Ingredient.CODEC*/.fieldOf("ingredient").forGetter(DryingRecipe::getInput),
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
        //?}
    }
}
