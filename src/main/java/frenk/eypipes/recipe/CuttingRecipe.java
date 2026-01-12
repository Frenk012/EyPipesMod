package frenk.eypipes.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import frenk.eypipes.registries.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * Custom recipe for cutting dried herbs into cutted herbs.
 * - Damages shears instead of consuming them
 * - Preserves fermentation level from input to output
 */
public class CuttingRecipe implements CraftingRecipe {

    private final Ingredient input;
    private final ItemStack output;
    private final int outputCount;

    public CuttingRecipe(Ingredient input, ItemStack output, int outputCount) {
        this.input = input;
        this.output = output;
        this.outputCount = outputCount;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasInput = false;
        boolean hasShears = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (this.input.test(stack)) {
                if (hasInput) return false; // Only one input allowed
                hasInput = true;
            } else if (stack.is(Items.SHEARS)) {
                if (hasShears) return false; // Only one shears allowed
                hasShears = true;
            } else {
                return false; // Unknown item
            }
        }

        return hasInput && hasShears;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // Find the input herb and copy its fermentation level
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && this.input.test(stack)) {
                ItemStack result = this.output.copy();
                result.setCount(this.outputCount);

                // Copy fermentation level from input to output
                Integer fermentationLevel = stack.get(ModDataComponents.FERMENTATION_LEVEL.get());
                if (fermentationLevel != null) {
                    result.set(ModDataComponents.FERMENTATION_LEVEL.get(), fermentationLevel);
                }

                // Copy fermentation start time if present
                Long fermentationStart = stack.get(ModDataComponents.FERMENTATION_START.get());
                if (fermentationStart != null) {
                    result.set(ModDataComponents.FERMENTATION_START.get(), fermentationStart);
                }

                return result;
            }
        }

        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
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
        list.add(Ingredient.of(Items.SHEARS));
        return list;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(Items.SHEARS)) {
                // Damage shears by 1 and return them
                ItemStack damagedShears = stack.copy();
                damagedShears.setDamageValue(damagedShears.getDamageValue() + 1);

                // Check if shears are broken
                if (damagedShears.getDamageValue() < damagedShears.getMaxDamage()) {
                    remaining.set(i, damagedShears);
                }
                // If broken, leave as EMPTY (shears are destroyed)
            }
        }

        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CUTTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        // Must return CRAFTING type for vanilla crafting table to find this recipe
        return RecipeType.CRAFTING;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
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
     * Serializer for CuttingRecipe
     */
    public static class Serializer implements RecipeSerializer<CuttingRecipe> {

        public static final MapCodec<CuttingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(CuttingRecipe::getInput),
                        ItemStack.STRICT_CODEC.fieldOf("output").forGetter(CuttingRecipe::getOutput),
                        Codec.INT.optionalFieldOf("count", 4).forGetter(CuttingRecipe::getOutputCount)
                ).apply(instance, CuttingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CuttingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CuttingRecipe::getInput,
                        ItemStack.STREAM_CODEC, CuttingRecipe::getOutput,
                        ByteBufCodecs.INT, CuttingRecipe::getOutputCount,
                        CuttingRecipe::new
                );

        @Override
        public MapCodec<CuttingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CuttingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
