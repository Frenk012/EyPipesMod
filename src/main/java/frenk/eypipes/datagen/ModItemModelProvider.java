package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Item model provider for EyPipes items.
 * Generates simple item models for non-animated items.
 * Note: Pipe and Cigar use GeckoLib models and don't need generated models.
 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EyPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Basic items with simple generated models
        basicItem(ModItems.ERBAPIPA.get());
        basicItem(ModItems.ERBAPIPA_DRIED.get());
        basicItem(ModItems.ERBAPIPA_CUTTED.get());
        basicItem(ModItems.ERBAPIPA_SEEDS.get());

        // Drying rack block item
        withExistingParent(ModItems.DRYING_RACK_ITEM.getId().getPath(),
                modLoc("block/drying_rack"));

        // Note: Pipe and Cigar use GeckoLib models defined in assets/geo/
        // They don't need generated models here
    }
}
