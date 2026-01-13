package frenk.eypipes.datagen;

import frenk.eypipes.recipe.HerbBundlePackingRecipe;
import frenk.eypipes.recipe.HerbBundleUnpackingRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Recipe builder for herb bundle packing/unpacking recipes.
 */
public class HerbBundleRecipeBuilder implements RecipeBuilder {

    private final Item inputItem;
    private final Item outputItem;
    private final boolean isPacking; // true = packing (9 herbs -> bundle), false = unpacking (bundle -> 9 herbs)
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private HerbBundleRecipeBuilder(Item inputItem, Item outputItem, boolean isPacking) {
        this.inputItem = inputItem;
        this.outputItem = outputItem;
        this.isPacking = isPacking;
    }

    /**
     * Create a packing recipe (9 dried herbs -> 1 bundle)
     */
    public static HerbBundleRecipeBuilder packing(Item driedHerb, Item bundleBlock) {
        return new HerbBundleRecipeBuilder(driedHerb, bundleBlock, true);
    }

    /**
     * Create an unpacking recipe (1 bundle -> 9 dried herbs)
     */
    public static HerbBundleRecipeBuilder unpacking(Item bundleBlock, Item driedHerb) {
        return new HerbBundleRecipeBuilder(bundleBlock, driedHerb, false);
    }

    @Override
    public HerbBundleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public HerbBundleRecipeBuilder group(@Nullable String group) {
        return this;
    }

    @Override
    public Item getResult() {
        return outputItem;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        Recipe<?> recipe;
        if (isPacking) {
            recipe = new HerbBundlePackingRecipe(inputItem, outputItem);
        } else {
            recipe = new HerbBundleUnpackingRecipe(inputItem, outputItem);
        }

        recipeOutput.accept(id, recipe, advancementBuilder.build(id.withPrefix("recipes/misc/")));
    }
}
