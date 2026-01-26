package frenk.eypipes.integration.jei;

import frenk.eypipes.recipe.CuttingBoardRecipe;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * JEI category for cutting board recipes.
 * Shows dried herb + knife -> cut herbs.
 */
public class CuttingBoardRecipeCategory implements IRecipeCategory<CuttingBoardRecipe> {

    private final IDrawable icon;
    private final Component title;
    private final IDrawableAnimated arrow;

    public CuttingBoardRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.CUTTING_BOARD_ITEM.get()));
        this.title = Component.translatable("jei.eypipes.category.cutting_board");
        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
    }

    @Override
    public RecipeType<CuttingBoardRecipe> getRecipeType() {
        return EyPipesJEIPlugin.CUTTING_BOARD_TYPE;
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
        return 120;
    }

    @Override
    public int getHeight() {
        return 34;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses) {
        // Input herb slot
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
                .addIngredients(recipe.getInput());

        // Knife slot (tool, not consumed)
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 9)
                .addItemStack(new ItemStack(ModItems.KNIFE.get()));

        // Output slot
        ItemStack outputStack = recipe.getOutput().copy();
        outputStack.setCount(recipe.getOutputCount());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 9)
                .addItemStack(outputStack);
    }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 49, 10);
    }
}
