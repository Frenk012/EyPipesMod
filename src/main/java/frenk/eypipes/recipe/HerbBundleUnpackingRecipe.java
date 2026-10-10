package frenk.eypipes.recipe;

import frenk.eypipes.registries.ModDataComponents;
import net.minecraft.core.NonNullList;
//? if <1.20.5 {
/*import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
*///?} else {
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Recipe for unpacking a bundle block into 9 dried herbs.
 * Preserves fermentation level from input bundle to output herbs.
 */
public class HerbBundleUnpackingRecipe implements CraftingRecipe {

    //? if <1.20.5 {
    /*// Until 1.20.2 a recipe carries its own id; the serializer sets it once decoded
    private ResourceLocation id;

    @Override
    public ResourceLocation getId() {
        return id;
    }
    *///?}

    private final Item inputBundle;
    private final Item outputHerb;

    public HerbBundleUnpackingRecipe(Item inputBundle, Item outputHerb) {
        this.inputBundle = inputBundle;
        this.outputHerb = outputHerb;
    }

    @Override
    //? if <1.20.5 {
    /*public boolean matches(CraftingContainer input, Level level) {
    *///?} else
    public boolean matches(CraftingInput input, Level level) {
        int bundleCount = 0;
        int totalItems = 0;

        for (int i = 0; i < size(input); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            totalItems++;
            if (stack.is(inputBundle)) {
                bundleCount++;
            }
        }

        // Must have exactly 1 bundle and nothing else
        return bundleCount == 1 && totalItems == 1;
    }

    @Override
    //? if <1.20.5 {
    /*public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
    *///?} else
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Find the bundle and get its fermentation level
        int fermentationLevel = ModDataComponents.QUALITY_DRIED;

        for (int i = 0; i < size(input); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(inputBundle)) {
                fermentationLevel = ModDataComponents.FERMENTATION_LEVEL.getOrDefault(stack, ModDataComponents.QUALITY_DRIED);
                break;
            }
        }

        // Create output herbs with same fermentation level
        ItemStack result = new ItemStack(outputHerb, 9);
        ModDataComponents.FERMENTATION_LEVEL.set(result, fermentationLevel);
        return result;
    }

    //? if <1.21.5 {
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    //? if <1.20.5 {
    /*public ItemStack getResultItem(RegistryAccess registries) {
    *///?} else
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(outputHerb, 9);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(inputBundle));
        return list;
    }
    //?}

    //? if >=1.21.5 {
    /*// Not auto-placeable from the recipe book: the nine inputs must all carry the same
    // fermentation level, which a placement hint cannot express. Crafting by hand still works.
    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }
    *///?}

    private static int size(/*? if <1.20.5 {*//*CraftingContainer*//*?} else {*/CraftingInput/*?}*/ input) {
        //? if <1.20.5 {
        /*return input.getContainerSize();
        *///?} else
        return input.size();
    }

    @Override
    //? if <1.20.5 {
    /*public RecipeSerializer<?> getSerializer() {
    *///?} else
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return ModRecipes.HERB_BUNDLE_UNPACKING_SERIALIZER.get();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    public Item getInputBundle() {
        return inputBundle;
    }

    public Item getOutputHerb() {
        return outputHerb;
    }

    /**
     * Serializer for HerbBundleUnpackingRecipe
     */
    public static class Serializer implements RecipeSerializer<HerbBundleUnpackingRecipe> {

        //? if <1.20.5 {
        /*@Override
        public HerbBundleUnpackingRecipe fromJson(ResourceLocation id, JsonObject json) {
            HerbBundleUnpackingRecipe recipe = new HerbBundleUnpackingRecipe(
                    BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "input"))),
                    BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "output"))));
            recipe.id = id;
            return recipe;
        }

        @Override
        public HerbBundleUnpackingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            HerbBundleUnpackingRecipe recipe = new HerbBundleUnpackingRecipe(
                    buffer.readById(BuiltInRegistries.ITEM), buffer.readById(BuiltInRegistries.ITEM));
            recipe.id = id;
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HerbBundleUnpackingRecipe recipe) {
            buffer.writeId(BuiltInRegistries.ITEM, recipe.getInputBundle());
            buffer.writeId(BuiltInRegistries.ITEM, recipe.getOutputHerb());
        }
        *///?} else {
        public static final MapCodec<HerbBundleUnpackingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec()
                                .fieldOf("input").forGetter(HerbBundleUnpackingRecipe::getInputBundle),
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec()
                                .fieldOf("output").forGetter(HerbBundleUnpackingRecipe::getOutputHerb)
                ).apply(instance, HerbBundleUnpackingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HerbBundleUnpackingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        net.minecraft.network.codec.ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM),
                        HerbBundleUnpackingRecipe::getInputBundle,
                        net.minecraft.network.codec.ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM),
                        HerbBundleUnpackingRecipe::getOutputHerb,
                        HerbBundleUnpackingRecipe::new
                );

        @Override
        public MapCodec<HerbBundleUnpackingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HerbBundleUnpackingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
        //?}
    }
}
