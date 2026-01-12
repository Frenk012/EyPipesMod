package frenk.eypipes.datagen;

import frenk.eypipes.recipe.CuttingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Recipe builder for cutting recipes.
 * Generates JSON files for CuttingRecipe.
 */
public class CuttingRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final Item output;
    private final int count;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private CuttingRecipeBuilder(Ingredient input, ItemLike output, int count) {
        this.input = input;
        this.output = output.asItem();
        this.count = count;
    }

    public static CuttingRecipeBuilder cutting(Ingredient input, ItemLike output, int count) {
        return new CuttingRecipeBuilder(input, output, count);
    }

    public static CuttingRecipeBuilder cutting(ItemLike input, ItemLike output, int count) {
        return new CuttingRecipeBuilder(Ingredient.of(input), output, count);
    }

    @Override
    public CuttingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public CuttingRecipeBuilder group(@Nullable String group) {
        // Cutting recipes don't use groups
        return this;
    }

    @Override
    public Item getResult() {
        return this.output;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        this.ensureValid(id);

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        // Create actual CuttingRecipe instance for proper serialization
        CuttingRecipe recipe = new CuttingRecipe(this.input, new ItemStack(this.output), this.count);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/misc/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}
