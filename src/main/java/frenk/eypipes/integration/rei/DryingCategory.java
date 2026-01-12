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
 * REI category for drying rack recipes.
 */
public class DryingCategory implements DisplayCategory<DryingDisplay> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "textures/gui/rei_drying.png");

    @Override
    public CategoryIdentifier<? extends DryingDisplay> getCategoryIdentifier() {
        return EyPipesREIPlugin.DRYING_CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.eypipes.category.drying");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.DRYING_RACK_ITEM.get());
    }

    @Override
    public List<Widget> setupDisplay(DryingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        Point startPoint = new Point(bounds.getCenterX() - 41, bounds.getCenterY() - 13);

        // Background
        widgets.add(Widgets.createRecipeBase(bounds));

        // Arrow
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 27, startPoint.y + 4)));

        // Input slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 4, startPoint.y + 5))
                .entries(display.getInputEntries().get(0))
                .markInput());

        // Output slot
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 61, startPoint.y + 5))
                .entries(display.getOutputEntries().get(0))
                .markOutput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 36;
    }
}
