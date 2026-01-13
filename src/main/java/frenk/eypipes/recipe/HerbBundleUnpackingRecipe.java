package frenk.eypipes.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import frenk.eypipes.registries.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Recipe for unpacking a bundle block into 9 dried herbs.
 * Preserves fermentation level from input bundle to output herbs.
 */
public class HerbBundleUnpackingRecipe implements CraftingRecipe {

    private final Item inputBundle;
    private final Item outputHerb;

    public HerbBundleUnpackingRecipe(Item inputBundle, Item outputHerb) {
        this.inputBundle = inputBundle;
        this.outputHerb = outputHerb;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int bundleCount = 0;
        int totalItems = 0;

        for (int i = 0; i < input.size(); i++) {
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Find the bundle and get its fermentation level
        int fermentationLevel = ModDataComponents.QUALITY_DRIED;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(inputBundle)) {
                fermentationLevel = stack.getOrDefault(ModDataComponents.FERMENTATION_LEVEL.get(), ModDataComponents.QUALITY_DRIED);
                break;
            }
        }

        // Create output herbs with same fermentation level
        ItemStack result = new ItemStack(outputHerb, 9);
        result.set(ModDataComponents.FERMENTATION_LEVEL.get(), fermentationLevel);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(outputHerb, 9);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(inputBundle));
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HERB_BUNDLE_UNPACKING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
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
    }
}
