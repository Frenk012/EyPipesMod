package frenk.eypipes.datagen;

import frenk.eypipes.recipe.CuttingBoardRecipe;
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
 * Recipe builder for cutting board recipes.
 */
public class CuttingBoardRecipeBuilder implements RecipeBuilder {

    private final Ingredient input;
    private final Item output;
    private final int count;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private CuttingBoardRecipeBuilder(Ingredient input, ItemLike output, int count) {
        this.input = input;
        this.output = output.asItem();
        this.count = count;
    }

    public static CuttingBoardRecipeBuilder cuttingBoard(Ingredient input, ItemLike output, int count) {
        return new CuttingBoardRecipeBuilder(input, output, count);
    }

    public static CuttingBoardRecipeBuilder cuttingBoard(ItemLike input, ItemLike output, int count) {
        return new CuttingBoardRecipeBuilder(Ingredient.of(input), output, count);
    }

    public static CuttingBoardRecipeBuilder cuttingBoard(ItemLike input, ItemLike output) {
        return new CuttingBoardRecipeBuilder(Ingredient.of(input), output, 4); // Default 4 output
    }

    @Override
    public CuttingBoardRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public CuttingBoardRecipeBuilder group(@Nullable String group) {
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

        CuttingBoardRecipe recipe = new CuttingBoardRecipe(this.input, new ItemStack(this.output), this.count);
        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/cutting_board/")));
    }

    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }
}
