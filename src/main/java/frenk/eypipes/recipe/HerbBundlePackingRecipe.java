package frenk.eypipes.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import frenk.eypipes.registries.ModDataComponents;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

    private final Item inputHerb;
    private final Item outputBundle;

    public HerbBundlePackingRecipe(Item inputHerb, Item outputBundle) {
        this.inputHerb = inputHerb;
        this.outputBundle = outputBundle;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.size() < 9) return false;

        int herbCount = 0;
        Integer fermentationLevel = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (!stack.is(inputHerb)) {
                return false; // Wrong item type
            }

            // Check fermentation level consistency
            int level1 = stack.getOrDefault(ModDataComponents.FERMENTATION_LEVEL.get(), ModDataComponents.QUALITY_DRIED);
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
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Find the fermentation level from input herbs
        int fermentationLevel = ModDataComponents.QUALITY_DRIED;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(inputHerb)) {
                fermentationLevel = stack.getOrDefault(ModDataComponents.FERMENTATION_LEVEL.get(), ModDataComponents.QUALITY_DRIED);
                break;
            }
        }

        // Create output bundle with same fermentation level
        ItemStack result = new ItemStack(outputBundle);
        result.set(ModDataComponents.FERMENTATION_LEVEL.get(), fermentationLevel);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(outputBundle);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.withSize(9, Ingredient.of(inputHerb));
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.HERB_BUNDLE_PACKING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
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
    }
}
