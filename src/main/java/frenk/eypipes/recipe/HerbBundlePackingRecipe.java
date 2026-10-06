package frenk.eypipes.recipe;

import frenk.eypipes.registries.ModDataComponents;
import frenk.eypipes.registries.ModItems;
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
 * Recipe for packing 9 dried herbs into a bundle block.
 * Preserves fermentation level from input herbs to output bundle.
 * All 9 herbs must have the same fermentation level.
 */
public class HerbBundlePackingRecipe implements CraftingRecipe {

    //? if <1.20.5 {
    /*// Until 1.20.2 a recipe carries its own id; the serializer sets it once decoded
    private ResourceLocation id;

    @Override
    public ResourceLocation getId() {
        return id;
    }
    *///?}

    private final Item inputHerb;
    private final Item outputBundle;

    public HerbBundlePackingRecipe(Item inputHerb, Item outputBundle) {
        this.inputHerb = inputHerb;
        this.outputBundle = outputBundle;
    }

    @Override
    //? if <1.20.5 {
    /*public boolean matches(CraftingContainer input, Level level) {
    *///?} else
    public boolean matches(CraftingInput input, Level level) {
        if (size(input) < 9) return false;

        int herbCount = 0;
        Integer fermentationLevel = null;

        for (int i = 0; i < size(input); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (!stack.is(inputHerb)) {
                return false; // Wrong item type
            }

            // Check fermentation level consistency
            int level1 = ModDataComponents.FERMENTATION_LEVEL.getOrDefault(stack, ModDataComponents.QUALITY_DRIED);
            if (fermentationLevel == null) {
                fermentationLevel = level1;
            } else if (fermentationLevel != level1) {
                return false; // Different fermentation levels - cannot mix
            }

            herbCount++;
        }

        return herbCount == 9;
    }

    @Override
    //? if <1.20.5 {
    /*public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
    *///?} else
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Find the fermentation level from input herbs
        int fermentationLevel = ModDataComponents.QUALITY_DRIED;

        for (int i = 0; i < size(input); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(inputHerb)) {
                fermentationLevel = ModDataComponents.FERMENTATION_LEVEL.getOrDefault(stack, ModDataComponents.QUALITY_DRIED);
                break;
            }
        }

        // Create output bundle with same fermentation level
        ItemStack result = new ItemStack(outputBundle);
        ModDataComponents.FERMENTATION_LEVEL.set(result, fermentationLevel);
        return result;
    }

    //? if <1.21.5 {
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    //? if <1.20.5 {
    /*public ItemStack getResultItem(RegistryAccess registries) {
    *///?} else
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(outputBundle);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.withSize(9, Ingredient.of(inputHerb));
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
        return ModRecipes.HERB_BUNDLE_PACKING_SERIALIZER.get();
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    public Item getInputHerb() {
        return inputHerb;
    }

    public Item getOutputBundle() {
        return outputBundle;
    }

    /**
     * Serializer for HerbBundlePackingRecipe
     */
    public static class Serializer implements RecipeSerializer<HerbBundlePackingRecipe> {

        //? if <1.20.5 {
        /*@Override
        public HerbBundlePackingRecipe fromJson(ResourceLocation id, JsonObject json) {
            HerbBundlePackingRecipe recipe = new HerbBundlePackingRecipe(
                    BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "input"))),
                    BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(GsonHelper.getAsString(json, "output"))));
            recipe.id = id;
            return recipe;
        }

        @Override
        public HerbBundlePackingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            HerbBundlePackingRecipe recipe = new HerbBundlePackingRecipe(
                    buffer.readById(BuiltInRegistries.ITEM), buffer.readById(BuiltInRegistries.ITEM));
            recipe.id = id;
            return recipe;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, HerbBundlePackingRecipe recipe) {
            buffer.writeId(BuiltInRegistries.ITEM, recipe.getInputHerb());
            buffer.writeId(BuiltInRegistries.ITEM, recipe.getOutputBundle());
        }
        *///?} else {
        public static final MapCodec<HerbBundlePackingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec()
                                .fieldOf("input").forGetter(HerbBundlePackingRecipe::getInputHerb),
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec()
                                .fieldOf("output").forGetter(HerbBundlePackingRecipe::getOutputBundle)
                ).apply(instance, HerbBundlePackingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HerbBundlePackingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        net.minecraft.network.codec.ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM),
                        HerbBundlePackingRecipe::getInputHerb,
                        net.minecraft.network.codec.ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM),
                        HerbBundlePackingRecipe::getOutputBundle,
                        HerbBundlePackingRecipe::new
                );

        @Override
        public MapCodec<HerbBundlePackingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HerbBundlePackingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
        //?}
    }
}
