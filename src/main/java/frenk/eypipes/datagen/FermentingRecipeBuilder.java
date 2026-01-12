package frenk.eypipes.datagen;

import frenk.eypipes.recipe.FermentingRecipe;
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
 * Recipe builder for tobacco jar fermenting recipes.
 */
public class FermentingRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final Item output;
    private final int fermentingTime;
    private final int targetQuality;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private FermentingRecipeBuilder(Ingredient input, ItemLike output, int fermentingTime, int targetQuality) {
        this.input = input;
        this.output = output.asItem();
        this.fermentingTime = fermentingTime;
        this.targetQuality = targetQuality;
    }

    public static FermentingRecipeBuilder fermenting(Ingredient input, ItemLike output, int fermentingTime, int targetQuality) {
        return new FermentingRecipeBuilder(input, output, fermentingTime, targetQuality);
    }

    public static FermentingRecipeBuilder fermenting(ItemLike input, ItemLike output, int fermentingTime, int targetQuality) {
        return new FermentingRecipeBuilder(Ingredient.of(input), output, fermentingTime, targetQuality);
    }

    /**
     * Create an "Aged" fermenting recipe (1 MC day = 24000 ticks, quality level 2)
     */
    public static FermentingRecipeBuilder aged(ItemLike input, ItemLike output) {
        return new FermentingRecipeBuilder(Ingredient.of(input), output, 24000, 2);
    }

    /**
     * Create a "Fermented" fermenting recipe (3 MC days = 72000 ticks, quality level 3)
     */
    public static FermentingRecipeBuilder fermented(ItemLike input, ItemLike output) {
        return new FermentingRecipeBuilder(Ingredient.of(input), output, 72000, 3);
    }

    @Override
    public FermentingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public FermentingRecipeBuilder group(@Nullable String group) {
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

        FermentingRecipe recipe = new FermentingRecipe(this.input, new ItemStack(this.output), this.fermentingTime, this.targetQuality);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/fermenting/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}
