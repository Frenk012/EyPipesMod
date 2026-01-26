package frenk.eypipes.integration.jei;

import frenk.eypipes.recipe.FermentingRecipe;
import frenk.eypipes.registries.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * JEI category for tobacco jar fermenting recipes.
 */
public class FermentingRecipeCategory implements IRecipeCategory<FermentingRecipe> {

    private final IDrawable icon;
    private final Component title;
    private final IDrawableAnimated arrow;

    public FermentingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.TOBACCO_JAR_ITEM.get()));
        this.title = Component.translatable("jei.eypipes.category.fermenting");
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    public RecipeType<FermentingRecipe> getRecipeType() {
        return EyPipesJEIPlugin.FERMENTING_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 100;
    }

    @Override
    public int getHeight() {
        return 50;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
                .addIngredients(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9)
                .addItemStack(recipe.getOutput());
    }

    @Override
    public void draw(FermentingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 27, 10);

        // Quality label
        String qualityLabel;
        if (recipe.getTargetQuality() == 3) {
            qualityLabel = "Fermented (3 days)";
        } else {
            qualityLabel = "Aged (1 day)";
        }
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                qualityLabel,
                0, 35, 0xFF404040, false
        );
    }
}
