package frenk.eypipes.datagen;

import frenk.eypipes.recipe.DryingRecipe;
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
 * Recipe builder for drying rack recipes.
 */
public class DryingRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final Item output;
    private final int dryingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private DryingRecipeBuilder(Ingredient input, ItemLike output, int dryingTime) {
        this.input = input;
        this.output = output.asItem();
        this.dryingTime = dryingTime;
    }

    public static DryingRecipeBuilder drying(Ingredient input, ItemLike output, int dryingTime) {
        return new DryingRecipeBuilder(input, output, dryingTime);
    }

    public static DryingRecipeBuilder drying(ItemLike input, ItemLike output, int dryingTime) {
        return new DryingRecipeBuilder(Ingredient.of(input), output, dryingTime);
    }

    public static DryingRecipeBuilder drying(ItemLike input, ItemLike output) {
        return new DryingRecipeBuilder(Ingredient.of(input), output, 6000); // Default 5 minutes
    }

    @Override
    public DryingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public DryingRecipeBuilder group(@Nullable String group) {
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

        DryingRecipe recipe = new DryingRecipe(this.input, new ItemStack(this.output), this.dryingTime);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/drying/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}
