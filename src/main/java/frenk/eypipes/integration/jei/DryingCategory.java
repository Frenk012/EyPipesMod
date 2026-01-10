package frenk.eypipes.integration.jei;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI recipe category for drying rack recipes.
 * Shows the erbapipa drying process.
 */
public class DryingCategory implements IRecipeCategory<DryingRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "drying");
    public static final RecipeType<DryingRecipe> RECIPE_TYPE = RecipeType.create(EyPipes.MOD_ID, "drying", DryingRecipe.class);

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "textures/gui/jei/drying.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final Component title;

    public DryingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 82, 34);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.DRYING_RACK_ITEM.get()));
        this.title = Component.translatable("jei.eypipes.category.drying");

        // Create animated arrow
        IDrawableStatic staticArrow = guiHelper.createDrawable(TEXTURE, 82, 0, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(staticArrow, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<DryingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DryingRecipe recipe, IFocusGroup focuses) {
        // Input slot
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 9)
                .addItemStack(recipe.input());

        // Output slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 9)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(DryingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Draw animated arrow
        arrow.draw(guiGraphics, 24, 9);
    }
}
