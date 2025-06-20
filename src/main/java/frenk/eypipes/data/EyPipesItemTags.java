package frenk.eypipes.data;

import frenk.eypipes.item.EyPipesItems;
import frenk.eypipes.EyPipesTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class EyPipesItemTags extends FabricTagProvider.ItemTagProvider {
    public EyPipesItemTags(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    protected void generateTags() {
        getOrCreateTagBuilder(EyPipesTags.Items.ERBAPIPA).add(EyPipesItems.ERBAPIPA);
        getOrCreateTagBuilder(EyPipesTags.Items.ERBAPIPA_SEEDS).add(EyPipesItems.ERBAPIPA_SEEDS);
        getOrCreateTagBuilder(EyPipesTags.Items.ERBAPIPA_DRIED).add(EyPipesItems.ERBAPIPA_DRIED);
    }
}
