package frenk.eypipes.integration.rei;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * REI category for cutting board recipes.
 * Shows dried herb + knife -> cutted herbs.
 */
public class CuttingBoardCategory implements DisplayCategory<CuttingBoardDisplay> {

    @Override
    public CategoryIdentifier<? extends CuttingBoardDisplay> getCategoryIdentifier() {
        return EyPipesREIPlugin.CUTTING_BOARD_CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.eypipes.category.cutting_board");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.CUTTING_BOARD_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(CuttingBoardDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        Point startPoint = new Point(bounds.getCenterX() - 50, bounds.getCenterY() - 13);

        // Background
        widgets.add(Widgets.createRecipeBase(bounds));

        // Input herb slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x, startPoint.y + 5))
                .entries(display.getInput())
                .markInput());

        // Plus sign
        widgets.add(Widgets.createLabel(new Point(startPoint.x + 22, startPoint.y + 9),
                Component.literal("+"))
                .noShadow()
                .color(0xFF404040, 0xFFBBBBBB));

        // Knife slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 30, startPoint.y + 5))
                .entries(display.getKnife())
                .markInput());

        // Arrow
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 52, startPoint.y + 4)));

        // Output slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 82, startPoint.y + 5))
                .entries(display.getOutput())
                .markOutput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }

    @Override
    public int getDisplayWidth(CuttingBoardDisplay display) {
        return 120;
    }
}
